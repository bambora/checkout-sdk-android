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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bambora.android.java.bamborasdk.Bambora
import com.bambora.android.java.demoapp.databinding.FragmentOverviewBinding

/**
 * Fragment that is only displayed when a payment has been successfully authorized by 3DS.
 * Contains all the logic to show the data from the successfully authorized payment.
 */
class OverviewFragment : Fragment() {

    private lateinit var binding: FragmentOverviewBinding
    private val authorizationDataViewModel: AuthorizationDataViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentOverviewBinding.inflate(inflater, container, false)
        initOverviewValues()
        return binding.root
    }

    private fun initOverviewValues() {
        binding.apply {
            with(authorizationDataViewModel.authorizationData) {
                transactionIdDataTextView.text = txnId
                orderIdDataTextView.text = orderId
                referenceDataTextView.text = reference
                amountDataTextView.text = amount
                currencyDataTextView.text = currency
                dateDataTextView.text = date
                timeDataTextView.text = time
                feeIdDataTextView.text = feeId
                transactionFeeDataTextView.text = txnFee
                paymentTypeDataTextView.text = paymentType
                walletName?.let {
                    walletDataTextView.text = walletName
                    walletDataTextView.setTextColor(resources.getColor(R.color.white))
                } ?: run {
                    walletDataTextView.text = "n.a."
                    walletDataTextView.setTextColor(resources.getColor(R.color.gray))
                }
                cardNumber?.let {
                    cardNumberDataTextView.text = cardNumber
                    cardNumberDataTextView.setTextColor(resources.getColor(R.color.white))
                } ?: run {
                    cardNumberDataTextView.text = "n.a."
                    cardNumberDataTextView.setTextColor(resources.getColor(R.color.gray))
                }
                expireMonth?.let {
                    expireMonthDataTextView.text = expireMonth
                    expireMonthDataTextView.setTextColor(resources.getColor(R.color.white))
                } ?: run {
                    expireMonthDataTextView.text = "n.a."
                    expireMonthDataTextView.setTextColor(resources.getColor(R.color.gray))
                }
                expireYear?.let {
                    expireYearDataTextView.text = expireYear
                    expireYearDataTextView.setTextColor(resources.getColor(R.color.white))
                } ?: run {
                    expireYearDataTextView.text = "n.a."
                    expireYearDataTextView.setTextColor(resources.getColor(R.color.gray))
                }
                subscriptionId?.let {
                    subscriptionIdDataTextView.text = subscriptionId
                    subscriptionIdDataTextView.setTextColor(resources.getColor(R.color.white))
                } ?: run {
                    subscriptionIdDataTextView.text = "n.a."
                    subscriptionIdDataTextView.setTextColor(resources.getColor(R.color.gray))
                }
                tokenId?.let {
                    tokenIdDataTextView.text = tokenId
                    tokenIdDataTextView.setTextColor(resources.getColor(R.color.white))
                } ?: run {
                    tokenIdDataTextView.text = "n.a."
                    tokenIdDataTextView.setTextColor(resources.getColor(R.color.gray))
                }
                eci?.let {
                    eciDataTextView.text = eci
                    eciDataTextView.setTextColor(resources.getColor(R.color.white))
                } ?: run {
                    eciDataTextView.text = "n.a."
                    eciDataTextView.setTextColor(resources.getColor(R.color.gray))
                }
                issuerCountry?.let {
                    issuerCountryDataTextView.text = issuerCountry
                    issuerCountryDataTextView.setTextColor(resources.getColor(R.color.white))
                } ?: run {
                    issuerCountryDataTextView.text = "n.a."
                    issuerCountryDataTextView.setTextColor(resources.getColor(R.color.gray))
                }
                hashDataTextView.text = hash

                additionalFields?.let {
                    val recyclerView: RecyclerView = binding.additionalFieldsRecyclerView
                    recyclerView.layoutManager = LinearLayoutManager(this@OverviewFragment.context)
                    recyclerView.adapter = AdditionalFieldAdapter(it)
                }
                
                newSessionButton.setOnClickListener {
                    if (Bambora.isInitialized) {
                        Bambora.close()
                    }
                    activity?.supportFragmentManager?.beginTransaction()?.apply {
                        replace(R.id.fragmentContainerView, SessionFragment())
                        commit()
                    }
                }
            }
        }
    }
}