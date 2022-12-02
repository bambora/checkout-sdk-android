/*
 * Copyright (c) 2022.  Bambora ( https://bambora.com/ )
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.bambora.android.java.bamborasdk

import android.content.Context
import android.content.Intent
import com.bambora.android.java.bamborasdk.extensions.isNetworkAvailable

/**
 *  The layer between your app and the SDK.
 */
class Checkout internal constructor(internal var sessionToken: String, private var appScheme: String, internal var baseUrl: String) {

    /**
     * Interface which should be provided to receive the events that are generated during the payment.
     */
    var checkoutEventReceiver: CheckoutEventReceiver? = null

    internal val returnUrl: String = "$appScheme://bamborasdk/return"
    internal val subscribedEvents = mutableListOf<EventType>()

    internal lateinit var bamboraCheckoutActivity: BamboraCheckoutActivity

    /**
     * Callback for WebView events.
     */
    internal val webViewEventCallback = object : WebViewEventCallback {
        override fun onJavascriptEventDispatched(eventType: String, jsonPayload: String) {
            processEvent(CheckoutEventMapper.mapEventType(eventType), jsonPayload)
        }

        override fun onWebViewClosed() {
            checkoutEventReceiver?.onEventDispatched(Event.CheckoutViewClose)
            closeBamboraCheckoutActivity()
        }

        override fun onWebViewError(errorCode: Int) {
            checkoutEventReceiver?.onEventDispatched(Event.Error(BamboraException.LoadSessionException))
            closeBamboraCheckoutActivity()
        }
    }

    /**
     * Displays the Bambora Checkout on top of the provided Activity.
     *
     * @param context Activity context where you want to launch the Bambora Checkout.
     */
    fun show(context: Context) {
        if (context.isNetworkAvailable()) {
            context.startActivity(Intent(context,BamboraCheckoutActivity::class.java))
        } else {
            checkoutEventReceiver?.onEventDispatched(Event.Error(BamboraException.InternetException))
        }
    }

    /**
     * Subscribe on all SDK events.
     */
    fun subscribeOnAllEvents() {
        EventType.values().forEach { eventType ->
            if (!subscribedEvents.contains(eventType)) {
                subscribedEvents.add(eventType)
            }
        }
    }

    /**
     * Subscribe on a single SDK event.
     *
     * @param event single [EventType].
     */
    fun on(event: EventType) {
        if (!subscribedEvents.contains(event)) subscribedEvents.add(event)
    }

    /**
     * Subscribe on multiple SDK events.
     *
     * @param events list of [EventType].
     */
    fun on(events: List<EventType>) {
        events.forEach { eventType ->
            if (!subscribedEvents.contains(eventType)) {
                subscribedEvents.add(eventType)
            }
        }
    }

    /**
     * Unsubscribe from a single SDK event.
     *
     * @param event single [EventType].
     */
    fun off(event: EventType) {
        if (subscribedEvents.contains(event)) subscribedEvents.remove(event)

    }

    /**
     * Unsubscribe from multiple SDK events.
     *
     * @param events list of [EventType].
     */
    fun off(events: List<EventType>) {
        events.forEach { eventType ->
            if (subscribedEvents.contains(eventType)) subscribedEvents.remove(eventType)
        }
    }

    /**
     * Notifies the host app, only when subscribed to this event.
     */
    private fun processEvent(eventType: EventType, jsonPayload: String) {
        if (subscribedEvents.contains(eventType)) {
            checkoutEventReceiver?.onEventDispatched(CheckoutEventMapper.mapEventObject(eventType, jsonPayload))
        }
    }

    /**
     * Closes the [BamboraCheckoutActivity].
     */
    internal fun closeBamboraCheckoutActivity() {
        bamboraCheckoutActivity.finish()
    }
}
