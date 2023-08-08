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

package com.bambora.android.java.checkoutsdk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Bambora Checkout Events.
 */
sealed class Event {

    object CheckoutViewClose : Event()

    data class ErrorEvent(val bamboraException: BamboraException) : Event()

    @Serializable(with = AuthorizeDeserializer::class)
    data class Authorize(
        val amount: String,
        val cardNumber: String?,
        val currency: String,
        val date: String,
        val eci: String?,
        val expireMonth: String?,
        val expireYear: String?,
        val feeId: String,
        val hash: String,
        val issuerCountry: String?,
        val orderId: String,
        val paymentType: String,
        val reference: String,
        val subscriptionId: String?,
        val time: String,
        val tokenId: String?,
        val txnFee: String,
        val txnId: String,
        val ui: String,
        val walletName: String?,
        val additionalFields: Map<String, String>?
    ) : Event()

    @Serializable
    data class CardTypeResolve(
        @SerialName("displayname")
        val displayName: String,
        val fee: Fee,
        @SerialName("groupid")
        val groupId: Int,
        val id: Int,
        val name: String,
        val priority: Int,
        val type: String
    ) : Event()

    data class PaymentTypeSelection(val paymentType: String) : Event()
}

@Serializable
data class Fee(
    @SerialName("addfee")
    val addFee: String,
    val amount: Int,
    val id: String
)

@Serializable
internal data class AuthorizeResponse(val acceptUrl: String, val data: Authorize) : Event()
