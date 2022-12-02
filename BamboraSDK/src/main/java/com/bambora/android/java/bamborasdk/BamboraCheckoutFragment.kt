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
import android.util.Base64
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.bambora.android.java.bamborasdk.databinding.FragmentBamboraCheckoutBinding
import com.bambora.android.java.bamborasdk.extensions.isAllowedDomain
import com.bambora.android.java.bamborasdk.extensions.isDeeplink
import com.bambora.android.java.bamborasdk.extensions.isPackageInstalled
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

/**
 * Fragment is a bottom sheet which shows the WebView on top of the host app.
 * This Fragment lives in the [BamboraCheckoutActivity].
 */
internal class BamboraCheckoutFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBamboraCheckoutBinding
    private lateinit var checkout: Checkout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        checkout = Bambora.checkout ?: throw BamboraException.SdkNotInitializedException

        binding = FragmentBamboraCheckoutBinding.inflate(inflater, container, false)
        initWebView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheet()
    }

    /**
     * Sends an onWebViewClosed event to the [Checkout] instance when the WebView is dismissed.
     */
    override fun onDestroy() {
        super.onDestroy()
        checkout.webViewEventCallback.onWebViewClosed()
    }

    private fun initBottomSheet() {
        this.dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as BottomSheetDialog
            bottomSheetDialog.apply {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.maxHeight = getMaxHeightOfBottomSheet()
            }

            val container: FrameLayout = binding.bamboraCheckoutContentContainer
            BottomSheetBehavior.from(container).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                peekHeight = getMaxHeightOfBottomSheet()
            }

            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.apply {
                layoutParams.height = getMaxHeightOfBottomSheet()
                requestLayout()
            }
        }
    }

    private fun getMaxHeightOfBottomSheet(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        val height = displayMetrics.heightPixels
        return (height * 0.80).toInt()
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

            loadUrl(constructCheckoutUrl())

        }
    }

    private fun constructCheckoutUrl(): String {
        return "${checkout.baseUrl}/${checkout.sessionToken}${BamboraConstants.CHECKOUT_WEB_VIEW_INLINE}#${getEncodedPaymentOptions()}"
    }

    /**
     * Opens the provided URL in the WebView.
     *
     * @param url The epayReturnUrl that should be opened in the WebView.
     *
     * @throws BamboraException.GenericException when the provided URL domain is not allowed.
     */
    fun openEpayReturnUrl(url: String) {
        if (url.isAllowedDomain()) {
            binding.bamboraCheckoutWebView.loadUrl(url)
        } else {
            throw BamboraException.GenericException
        }
    }

    /**
     * @return The [PaymentOptions] as a Base64 encoded JSON string.
     */
    private fun getEncodedPaymentOptions(): String {
        val paymentOptions = PaymentOptions(BuildConfig.VERSION_NAME, checkout.returnUrl, getInstalledWalletProducts().map { it.productName })
        val jsonString = Json.encodeToString(paymentOptions)
        return Base64.encodeToString(jsonString.toByteArray(
            StandardCharsets.UTF_8), Base64.NO_WRAP or Base64.URL_SAFE)
    }

    private fun getInstalledWalletProducts(): List<WalletProduct> {
        return WalletProduct.values().toList().filter {
            context?.isPackageInstalled(it.packageName) == true
        }
    }
}