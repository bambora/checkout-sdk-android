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
import com.bambora.android.java.bamborasdk.extensions.isAllowedDomain

/**
 * Activity where the return URL deep link comes in, it determines what action to pursue based on whether the [Checkout] is initialized or not.
 * If the SDK was closed, the Activity redirects to the [BamboraReturnFragment] which shows a full-screen WebView.
 * If the SDK was not closed, the Activity redirects to the [BamboraCheckoutActivity] which shows a WebView in a bottom sheet.
 */
internal class BamboraReturnActivity : AppCompatActivity() {

    /**
     * @throws BamboraException.GenericException if 'epayreturn' query parameter is null **or** if the epayReturnUrl is not from an allowed domain.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val epayReturnUrl = intent?.data?.getQueryParameter("epayreturn")
            ?: throw BamboraException.GenericException

        if (!epayReturnUrl.isAllowedDomain()) {
            throw BamboraException.GenericException
        }

        if (Bambora.isInitialized) {
            val intent = Intent(this, BamboraCheckoutActivity::class.java)
            intent.putExtra("epayReturnUrl", epayReturnUrl)
            startActivity(intent)
        } else {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, BamboraReturnFragment(epayReturnUrl))
                .commit()
        }
    }
}
