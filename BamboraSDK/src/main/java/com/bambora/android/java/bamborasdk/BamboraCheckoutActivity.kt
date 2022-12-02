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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Container activity for showing the [BamboraCheckoutFragment] as a bottom sheet.
 * This Activity is also loaded when the [Checkout] is initialized and a return URL is received.
 */
internal class BamboraCheckoutActivity : AppCompatActivity() {

    private lateinit var bamboraCheckoutFragment: BamboraCheckoutFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Bambora.checkout?.bamboraCheckoutActivity = this
        bamboraCheckoutFragment = BamboraCheckoutFragment().apply {
            show(supportFragmentManager, null)
        }
    }

    /**
     * Instructs the fragment to open the epayReturnUrl that was received.
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val epayReturnUrl = intent?.extras?.getString("epayReturnUrl")
        if (epayReturnUrl != null) {
            bamboraCheckoutFragment.openEpayReturnUrl(epayReturnUrl)
        }
    }
}
