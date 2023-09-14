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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bambora.android.java.demoapp.databinding.ActivityMainBinding

/**
 * The initial activity of the app.
 *
 * If it is redirected to from a wallet app, it will have a deeplink URL in its intent data.
 * If the deeplink URL is not null, it will process the deeplink based on whether the Checkout is initialized or not.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    internal val bamboraSDKHelper = BamboraSDKHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openSessionFragment()

        val deeplink = intent?.dataString
        if (deeplink != null) {
            bamboraSDKHelper.processDeeplink(deeplink)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val deeplink = intent?.dataString
        if (deeplink != null) {
            bamboraSDKHelper.processDeeplink(deeplink)
        }
    }

    internal fun openSessionFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, SessionFragment())
            commit()
        }
    }

    internal fun openOverviewFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, OverviewFragment())
            commit()
        }
    }
}
