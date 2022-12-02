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

/**
 * Entrypoint of the Bambora Checkout SDK.
 */
object Bambora {

    /**
     * The Checkout instance.
     * Use this instance to interact with the Checkout.
     * Initialize using [checkout].
     */
    var checkout: Checkout? = null
        private set

    /**
     * Returns whether the Checkout is currently initialized or not.
     */
    val isInitialized: Boolean
        get() {
            return checkout != null
        }

    /**
     *  Initialize checkout SDK
     *
     * @param sessionToken The token that you receive when creating a session.
     * @param appScheme The app scheme you have configured in your Manifest.
     * @param customUrl Optional. A custom base URL to connect to the Bambora backend. If not provided, the SDK will use the default url.
     *
     * @return An instance of Checkout, use this instance to interact with the Checkout.
     * @throws BamboraException.SdkAlreadyInitializedException when the SDK is already initialized.
     */
    fun checkout(sessionToken: String, appScheme: String, customUrl: String = BamboraConstants.CHECKOUT_BASE_URL): Checkout {
        if (isInitialized) {
            throw BamboraException.SdkAlreadyInitializedException
        }

        checkout = Checkout(sessionToken, appScheme, customUrl)
        return checkout ?: throw BamboraException.GenericException
    }

    /**
     * Closes the Bambora Checkout and makes the Checkout instance null.
     */
    fun close() {
        checkout?.closeBamboraCheckoutActivity()
        checkout = null
    }
}
