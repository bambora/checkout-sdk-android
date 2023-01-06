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

import com.bambora.android.java.bamborasdk.extensions.isEmptyCheck
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
@Serializer(forClass = Event.Authorize::class)
internal object AuthorizeDeserializer : KSerializer<Event.Authorize> {
    private val stringToStringElementSerializer = MapSerializer(String.serializer(), String.serializer())

    override fun deserialize(decoder: Decoder): Event.Authorize {
        require(decoder is JsonDecoder)
        val jsonElement = decoder.decodeJsonElement()
        val authorizeMap = Json.decodeFromJsonElement(stringToStringElementSerializer, jsonElement)

        val knownFields = AuthorizeKeys.listOfAuthorizeKeys
        val additionalFields = authorizeMap.filter { (key, _) -> !knownFields.contains(key) }

        val amount = authorizeMap[AuthorizeKeys.AMOUNT.keyValue] ?: throw BamboraException.GenericException
        val cardNumber = authorizeMap[AuthorizeKeys.CARD_NUMBER.keyValue]
        val currency = authorizeMap[AuthorizeKeys.CURRENCY.keyValue] ?: throw BamboraException.GenericException
        val date = authorizeMap[AuthorizeKeys.DATE.keyValue] ?: throw BamboraException.GenericException
        val eci = authorizeMap[AuthorizeKeys.ECI.keyValue]
        val expireMonth = authorizeMap[AuthorizeKeys.EXPIRE_MONTH.keyValue]
        val expireYear = authorizeMap[AuthorizeKeys.EXPIRE_YEAR.keyValue]
        val feeId = authorizeMap[AuthorizeKeys.FEE_ID.keyValue] ?: throw BamboraException.GenericException
        val hash = authorizeMap[AuthorizeKeys.HASH.keyValue] ?: throw BamboraException.GenericException
        val issuerCountry = authorizeMap[AuthorizeKeys.ISSUER_COUNTRY.keyValue]
        val orderId = authorizeMap[AuthorizeKeys.ORDER_ID.keyValue] ?: throw BamboraException.GenericException
        val paymentType = authorizeMap[AuthorizeKeys.PAYMENT_TYPE.keyValue] ?: throw BamboraException.GenericException
        val reference = authorizeMap[AuthorizeKeys.REFERENCE.keyValue] ?: throw BamboraException.GenericException
        val subscriptionId = authorizeMap[AuthorizeKeys.SUBSCRIPTION_ID.keyValue]
        val time = authorizeMap[AuthorizeKeys.TIME.keyValue] ?: throw BamboraException.GenericException
        val tokenId = authorizeMap[AuthorizeKeys.TOKEN_ID.keyValue]
        val txnFee = authorizeMap[AuthorizeKeys.TXN_FEE.keyValue] ?: throw BamboraException.GenericException
        val txnId = authorizeMap[AuthorizeKeys.TXN_ID.keyValue] ?: throw BamboraException.GenericException
        val ui = authorizeMap[AuthorizeKeys.UI.keyValue] ?: throw BamboraException.GenericException
        val walletName = authorizeMap[AuthorizeKeys.WALLET_NAME.keyValue]

        return Event.Authorize(amount, cardNumber, currency, date, eci, expireMonth, expireYear, feeId, hash, issuerCountry, orderId, paymentType, reference, subscriptionId, time, tokenId, txnFee, txnId, ui, walletName, additionalFields.isEmptyCheck())
    }

    private enum class AuthorizeKeys(val keyValue: String) {
        AMOUNT("amount"),
        CARD_NUMBER("cardno"),
        CURRENCY("currency"),
        DATE("date"),
        ECI("eci"),
        EXPIRE_MONTH("expmonth"),
        EXPIRE_YEAR("expyear"),
        FEE_ID("feeid"),
        HASH("hash"),
        ISSUER_COUNTRY("issuercountry"),
        ORDER_ID("orderid"),
        PAYMENT_TYPE("paymenttype"),
        REFERENCE("reference"),
        SUBSCRIPTION_ID("subscriptionid"),
        TIME("time"),
        TOKEN_ID("tokenid"),
        TXN_FEE("txnfee"),
        TXN_ID("txnid"),
        UI("ui"),
        WALLET_NAME("walletname");

        companion object {
            val listOfAuthorizeKeys: List<String>
                get() {
                    return values().map {
                        it.keyValue
                    }
                }
        }
    }
}
