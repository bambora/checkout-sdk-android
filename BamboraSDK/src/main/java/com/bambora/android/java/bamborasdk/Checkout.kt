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
import android.util.Base64
import com.bambora.android.java.bamborasdk.extensions.isNetworkAvailable
import com.bambora.android.java.bamborasdk.extensions.isPackageInstalled
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

/**
 *  The layer between your app and the SDK.
 */
class Checkout {

    /**
     * Interface which should be provided to receive the events that are generated during the payment.
     */
    var checkoutEventReceiver: CheckoutEventReceiver? = null

    private var checkoutUrl: String
    internal lateinit var returnUrl: String

    internal val subscribedEvents = mutableListOf<EventType>()

    internal lateinit var bamboraCheckoutActivity: BamboraCheckoutActivity

    internal constructor(sessionToken: String, appScheme: String, baseUrl: String, context: Context) {
        this.returnUrl = "$appScheme://bamborasdk/return"
        this.checkoutUrl = constructCheckoutUrl(baseUrl, sessionToken, context)
    }

    internal constructor(epayReturnUrl: String) {
        this.checkoutUrl = epayReturnUrl
    }

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
            val intent = Intent(context, BamboraCheckoutActivity::class.java)
            intent.putExtra("urlToOpen", checkoutUrl)

            context.startActivity(intent)
        } else {
            checkoutEventReceiver?.onEventDispatched(Event.Error(BamboraException.InternetException))
        }
    }

    private fun constructCheckoutUrl(baseUrl: String, sessionToken: String, context: Context): String {
        return "${baseUrl}/${sessionToken}${BamboraConstants.CHECKOUT_WEB_VIEW_INLINE}#${getEncodedPaymentOptions(context)}"
    }

    /**
     * @return The [PaymentOptions] as a Base64 encoded JSON string.
     */
    private fun getEncodedPaymentOptions(context: Context): String {
        val paymentOptions = PaymentOptions(BuildConfig.VERSION_NAME, returnUrl, getInstalledWalletProducts(context).map { it.productName })
        val jsonString = Json.encodeToString(paymentOptions)
        return Base64.encodeToString(jsonString.toByteArray(
            StandardCharsets.UTF_8), Base64.NO_WRAP or Base64.URL_SAFE)
    }

    private fun getInstalledWalletProducts(context: Context): List<WalletProduct> {
        return WalletProduct.values().toList().filter {
            context.isPackageInstalled(it.packageName)
        }
    }

    internal fun setEpayReturnUrl(epayReturnUrl: String) {
        this.checkoutUrl = epayReturnUrl
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
