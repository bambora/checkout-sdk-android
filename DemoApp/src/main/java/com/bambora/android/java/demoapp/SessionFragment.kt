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

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bambora.android.java.bamborasdk.Bambora
import com.bambora.android.java.bamborasdk.Checkout
import com.bambora.android.java.bamborasdk.CheckoutEventReceiver
import com.bambora.android.java.bamborasdk.Event
import com.bambora.android.java.demoapp.databinding.FragmentSessionBinding

/**
 * Fragment that is displayed initially.
 * Allows users to enter a session id, a custom base URL and to start a payment session.
 */
class SessionFragment : Fragment() {

    private lateinit var binding: FragmentSessionBinding
    private lateinit var checkout: Checkout
    private val authorizationDataViewModel: AuthorizationDataViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        binding = FragmentSessionBinding.inflate(layoutInflater)
        initOpenCheckoutButton()
        loadCustomUrlText()
        loadCheckboxStatus()
        initCustomUrlCheckbox()
        return binding.root
    }

    private fun initOpenCheckoutButton() {
        binding.apply {
            openCheckout.setOnClickListener {
                sessionIdLayout.isErrorEnabled = false
                customUrlLayout.isErrorEnabled = false
                if (isValidInput()) {
                    initBamboraCheckout(
                        getSecureBaseUrl(customUrlInput.text.toString()),
                        sessionIdInput.text.toString()
                    )
                }
            }
        }
    }

    private fun initCustomUrlCheckbox() {
        binding.apply {
            checkboxCustomUrl.setOnClickListener { view ->
                if (view is CheckBox) {
                    setCustomURLVisibility()
                }
            }
        }
    }

    private fun setCustomURLVisibility() {
        binding.apply {
            if (checkboxCustomUrl.isChecked) {
                customUrlLayout.visibility = View.VISIBLE
            } else {
                customUrlLayout.visibility = View.GONE
            }
        }
    }

    /**
     * Retrieves the custom base URL from Shared Preferences.
     * The returned value is set in the custom base URL TextField.
     */
    private fun loadCustomUrlText() {
        val customURL = loadFromSharedPreferences(CUSTOM_BASE_URL_KEY)
        binding.customUrlInput.setText(customURL ?: "")
    }

    /**
     * Retrieves the checkbox status from Shared Preferences.
     * Based on the returned value, the checkbox is checked or unchecked.
     */
    private fun loadCheckboxStatus() {
        val checkboxStatus = loadFromSharedPreferences(CHECKBOX_STATUS).toBoolean()
        binding.checkboxCustomUrl.isChecked = checkboxStatus
        setCustomURLVisibility()
    }

    private fun loadFromSharedPreferences(key: String): String? {
        context?.let { context ->
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(key, "")
        }
        return null
    }

    private fun isValidInput(): Boolean {
        var isValid = true

        binding.apply {
            if (sessionIdInput.text.toString().isBlank()) {
                sessionIdLayout.error = getString(R.string.session_id_error)
                sessionIdLayout.isErrorEnabled = true
                isValid = false
            }

            if (checkboxCustomUrl.isChecked) {
                if (customUrlInput.text.toString().isBlank()) {
                    customUrlLayout.error = getString(R.string.custom_url_empty_error)
                    customUrlLayout.isErrorEnabled = true
                    return false
                }
                if (!Patterns.WEB_URL.matcher(binding.customUrlInput.text.toString()).matches()) {
                    customUrlLayout.error = getString(R.string.custom_url_invalid_error)
                    customUrlLayout.isErrorEnabled = true
                    isValid = false
                }
            }
        }

        return isValid
    }

    private fun saveToSharedPreferences(key: String, value: String) {
        context?.let { context ->
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.commit()
        }
    }

    /**
     * Returns the custom base URL with HTTPS.
     */
    private fun getSecureBaseUrl(baseURL: String): String {
        val checkboxIsChecked = binding.checkboxCustomUrl.isChecked

        if (!checkboxIsChecked) {
            saveToSharedPreferences(CHECKBOX_STATUS, checkboxIsChecked.toString())
        }

        val secureBaseUrl = if (baseURL.startsWith("https://")) baseURL else "https://$baseURL"

        if (secureBaseUrl.isNotBlank()) {
            saveToSharedPreferences(CUSTOM_BASE_URL_KEY, secureBaseUrl)
        }

        saveToSharedPreferences(CHECKBOX_STATUS, checkboxIsChecked.toString())

        return secureBaseUrl
    }

    /**
     * Initializes the Bambora Checkout with the use of the custom base URL and session id, and establishes a callback function for when a Bambora [Event] is received.
     */
    private fun initBamboraCheckout(baseURL: String, sessionId: String) {
        context?.let { applicationContext ->
            checkout = if (binding.checkboxCustomUrl.isChecked) {
                Bambora.checkout(sessionId, APP_SCHEME, baseURL)
            } else {
                Bambora.checkout(sessionId, APP_SCHEME)
            }

            checkout.apply {
                subscribeOnAllEvents()
                show(applicationContext)
                checkoutEventReceiver = object : CheckoutEventReceiver {
                    override fun onEventDispatched(event: Event) {
                        processReceivedEvent(event)
                        Log.i(SessionFragment::class.java.simpleName, "onEventDispatched: $event")
                    }
                }
            }
        }
    }

    private fun openOverviewFragment() {
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.fragmentContainerView, OverviewFragment())
            commit()
        }
    }

    /**
     * Determines how to process the [Event] that is received from the SDK.
     */
    private fun processReceivedEvent(event: Event) {
        when (event) {
            is Event.Authorize -> {
                authorizationDataViewModel.authorizationData = event
                openOverviewFragment()
            }
            is Event.CheckoutViewClose -> {
                Bambora.close()
            }
            else -> {}
        }

    }

    private companion object {
        const val APP_SCHEME = "bamborademoapp"
        const val PREFS_NAME = "DemoAppPreferences"
        const val CUSTOM_BASE_URL_KEY = "CustomBaseURL"
        const val CHECKBOX_STATUS = "CheckboxStatus"
    }
}
