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

package com.bambora.android.java.demoapp

import com.bambora.android.java.checkoutsdk.Bambora
import com.bambora.android.java.checkoutsdk.Checkout
import com.bambora.android.java.checkoutsdk.CheckoutEventReceiver
import com.bambora.android.java.checkoutsdk.Event

class BamboraSDKHelper(private val context: MainActivity) {
    private lateinit var checkout: Checkout
    internal lateinit var authorizationData: Event.Authorize

    /**
     * Callback function for when a Bambora [Event] is received.
     */
    private val myCheckoutEventReceiver = object : CheckoutEventReceiver {
        override fun onEventDispatched(event: Event) {
            processReceivedEvent(event)
        }
    }

    /**
     * Initializes the Bambora Checkout with the use of the session id.
     */
    internal fun openCheckout(token: String) {
        checkout = Bambora.checkout(token, APP_SCHEME, context)

        setupCheckoutDelegate()
        checkout.show(context)
    }

    /**
     * Initializes the Bambora Checkout with the use of the session id and connects to the provided custom endpoint.
     */
    internal fun openCheckout(token: String, customURL: String) {
        checkout = Bambora.checkout(token, APP_SCHEME, context, customURL)

        setupCheckoutDelegate()
        checkout.show(context)
    }

    /**
     * Allows the SDK to handle the received deeplink return, coming from a wallet payment or 3DS challenge.
     * Note that if the app was killed while the user was completing the wallet payment, the SDK will no longer be
     * initialized, and the event delegate has to be re-configured. This is why [checkout] is re-assigned, and
     * [setupCheckoutDelegate] is called again.
     */
    internal fun processDeeplink(deeplink: String) {
        checkout = Bambora.checkoutAfterReturn(deeplink)
        setupCheckoutDelegate()

        checkout.show(context)
    }

    /**
     * Assigns a delegate to the [Checkout] and subscribes to all events.
     */
    private fun setupCheckoutDelegate() {
        checkout.apply {
            checkoutEventReceiver = myCheckoutEventReceiver
            subscribeOnAllEvents()
        }
    }

    /**
     * Determines how to process the [Event] that is received from the SDK.
     */
    private fun processReceivedEvent(event: Event) {
        when (event) {
            is Event.Authorize -> {
                authorizationData = event
                context.openOverviewFragment()
            }
            is Event.CheckoutViewClose -> {
                Bambora.close()
            }
            else -> {}
        }
    }

    private companion object {
        const val APP_SCHEME = "bamborademoapp"
    }
}
