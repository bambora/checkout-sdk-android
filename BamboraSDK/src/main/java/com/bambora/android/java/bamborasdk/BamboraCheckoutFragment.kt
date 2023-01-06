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

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bambora.android.java.bamborasdk.databinding.BamboraCheckoutBinding
import com.bambora.android.java.bamborasdk.extensions.isDeeplink
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Fragment is a bottom sheet which shows the WebView on top of the host app.
 * This Fragment lives in the [BamboraCheckoutActivity].
 *
 * It is intended to be loaded when initializing a payment session.
 * It is also intended to be loaded when a return URL is received.
 */
internal class BamboraCheckoutFragment(private val url: String) : BottomSheetDialogFragment() {

    private lateinit var binding: BamboraCheckoutBinding
    private lateinit var checkout: Checkout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        checkout = Bambora.checkout ?: throw BamboraException.SdkNotInitializedException

        binding = BamboraCheckoutBinding.inflate(inflater, container, false)
        initWebView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        BottomSheetHelper.initBottomSheet(dialog, binding.bamboraCheckoutContentContainer, displayMetrics)
    }

    /**
     * Sends an onWebViewClosed event to the [Checkout] instance when the WebView is dismissed.
     */
    override fun onDestroy() {
        super.onDestroy()
        checkout.webViewEventCallback.onWebViewClosed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.bamboraCheckoutWebView.apply {
            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    checkout.webViewEventCallback.onWebViewError(
                        error?.errorCode ?: -1
                    )
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (request?.url?.isDeeplink() == true) {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = request.url
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }

                        // If the request URL is a deeplink & there is no app installed that can handle the deeplink, it skips this step
                        if (activity?.packageManager?.let { intent.resolveActivity(it) } != null) {
                            startActivity(intent)
                        }

                        return true
                    }
                    return false
                }
            }

            settings.javaScriptEnabled = true
            addJavascriptInterface(
                CheckoutJavascriptInterface(checkout.webViewEventCallback),
                BamboraConstants.CHECKOUT_JAVASCRIPT_INTERFACE_NAME
            )

            openUrl()
        }
    }

    /**
     * Opens the provided URL in the WebView.
     *
     * @param url The URL that should be opened in the WebView. If no URL is provided, the URL provided in the [BamboraCheckoutFragment] constructor will be opened.
     */
    internal fun openUrl(url: String = this.url) {
        binding.bamboraCheckoutWebView.loadUrl(url)
    }
}
