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

import com.bambora.android.java.bamborasdk.BamboraException
import com.bambora.android.java.bamborasdk.CheckoutEventMapper
import com.bambora.android.java.bamborasdk.Event
import com.bambora.android.java.bamborasdk.EventType
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import kotlin.test.assertFailsWith

class AuthorizeDecoderTest {

    @Test
    fun decode_authorize_without_additional_fields_success() {
        val jsonResponse = """
        {
         "acceptUrl":"https://example.org/accept",
         "data": {
            "ui":"inline",
            "txnid":"123456789012345678",
            "orderid":"1234",
            "reference":"987654321098",
            "amount":"100",
            "currency":"DKK",
            "date":"20221123",
            "time":"0934",
            "subscriptionid":"123456",
            "feeid":"54321",
            "txnfee":"0",
            "expmonth":"2",
            "expyear":"25",
            "paymenttype":"2",
            "cardno":"123456XXXXXX1234",
            "eci":"1",
            "issuercountry":"DNK",
            "tokenid":"a1b2c3d4e5f6",
            "hash":"a1b23c4d56789e0fg1h234567890ijk1",
            "walletname":"MobilePay"
          }
        }
        """

        val event = CheckoutEventMapper.mapEventObject(EventType.AUTHORIZE, jsonResponse)

        assertTrue(event is Event.Authorize)

        val authorizeEvent = event as Event.Authorize

        assertEquals("100", authorizeEvent.amount)
        assertEquals("123456XXXXXX1234", authorizeEvent.cardNumber)
        assertEquals("DKK", authorizeEvent.currency)
        assertEquals("20221123", authorizeEvent.date)
        assertEquals("1", authorizeEvent.eci)
        assertEquals("2", authorizeEvent.expireMonth)
        assertEquals("25", authorizeEvent.expireYear)
        assertEquals("54321", authorizeEvent.feeId)
        assertEquals("a1b23c4d56789e0fg1h234567890ijk1", authorizeEvent.hash)
        assertEquals("DNK", authorizeEvent.issuerCountry)
        assertEquals("1234", authorizeEvent.orderId)
        assertEquals("2", authorizeEvent.paymentType)
        assertEquals("987654321098", authorizeEvent.reference)
        assertEquals("123456", authorizeEvent.subscriptionId)
        assertEquals("0934", authorizeEvent.time)
        assertEquals("a1b2c3d4e5f6", authorizeEvent.tokenId)
        assertEquals("0", authorizeEvent.txnFee)
        assertEquals("123456789012345678", authorizeEvent.txnId)
        assertEquals("inline", authorizeEvent.ui)
        assertEquals("MobilePay", authorizeEvent.walletName)
        assertNull(authorizeEvent.additionalFields)
    }

    @Test
    fun decode_authorize_without_additional_fields_missing_nullable_fields_success() {
        val jsonResponse = """
        {
         "acceptUrl":"https://example.org/accept",
         "data": {
            "ui":"inline",
            "txnid":"123456789012345678",
            "orderid":"1234",
            "reference":"987654321098",
            "amount":"100",
            "currency":"DKK",
            "date":"20221123",
            "time":"0934",
            "feeid":"54321",
            "txnfee":"0",
            "paymenttype":"2",
            "hash":"a1b23c4d56789e0fg1h234567890ijk1"
          }
        }
        """

        val event = CheckoutEventMapper.mapEventObject(EventType.AUTHORIZE, jsonResponse)

        assertTrue(event is Event.Authorize)

        val authorizeEvent = event as Event.Authorize

        assertEquals("100", authorizeEvent.amount)
        assertNull(authorizeEvent.cardNumber)
        assertEquals("DKK", authorizeEvent.currency)
        assertEquals("20221123", authorizeEvent.date)
        assertNull(authorizeEvent.eci)
        assertNull(authorizeEvent.expireMonth)
        assertNull(authorizeEvent.expireYear)
        assertEquals("54321", authorizeEvent.feeId)
        assertEquals("a1b23c4d56789e0fg1h234567890ijk1", authorizeEvent.hash)
        assertNull(authorizeEvent.issuerCountry)
        assertEquals("1234", authorizeEvent.orderId)
        assertEquals("2", authorizeEvent.paymentType)
        assertEquals("987654321098", authorizeEvent.reference)
        assertNull(authorizeEvent.subscriptionId)
        assertEquals("0934", authorizeEvent.time)
        assertNull(authorizeEvent.tokenId)
        assertEquals("0", authorizeEvent.txnFee)
        assertEquals("123456789012345678", authorizeEvent.txnId)
        assertEquals("inline", authorizeEvent.ui)
        assertNull(authorizeEvent.walletName)
        assertNull(authorizeEvent.additionalFields)
    }

    @Test
    fun decode_authorize_without_additional_fields_missing_nonnullable_field_failed() {
        val jsonResponse = """
        {
         "acceptUrl":"https://example.org/accept",
         "data": {
            "ui":"inline",
            "txnid":"123456789012345678",
            "reference":"987654321098",
            "amount":"100",
            "currency":"DKK",
            "date":"20221123",
            "time":"0934",
            "subscriptionid":"123456",
            "feeid":"54321",
            "txnfee":"0",
            "expmonth":"2",
            "expyear":"25",
            "paymenttype":"2",
            "cardno":"123456XXXXXX1234",
            "eci":"1",
            "issuercountry":"DNK",
            "tokenid":"a1b2c3d4e5f6",
            "hash":"a1b23c4d56789e0fg1h234567890ijk1",
            "walletname":"MobilePay"
          }
        }
        """

         assertFailsWith<BamboraException.GenericException> {
             val event = CheckoutEventMapper.mapEventObject(EventType.AUTHORIZE, jsonResponse)
         }
    }

    @Test
    fun decode_authorize_with_additional_fields_success() {
        val jsonResponse = """
        {
         "acceptUrl":"https://example.org/accept",
         "data": {
            "ui":"inline",
            "txnid":"123456789012345678",
            "orderid":"1234",
            "reference":"987654321098",
            "amount":"100",
            "currency":"DKK",
            "date":"20221123",
            "time":"0934",
            "subscriptionid":"123456",
            "feeid":"54321",
            "txnfee":"0",
            "expmonth":"2",
            "expyear":"25",
            "paymenttype":"2",
            "cardno":"123456XXXXXX1234",
            "eci":"1",
            "issuercountry":"DNK",
            "tokenid":"a1b2c3d4e5f6",
            "hash":"a1b23c4d56789e0fg1h234567890ijk1",
            "walletname":"MobilePay",
            "extra1":"extraValue1",
            "extra2":"extraValue2"
          }
        }
        """

        val event = CheckoutEventMapper.mapEventObject(EventType.AUTHORIZE, jsonResponse)

        assertTrue(event is Event.Authorize)

        val authorizeEvent = event as Event.Authorize

        assertEquals("100", authorizeEvent.amount)
        assertEquals("123456XXXXXX1234", authorizeEvent.cardNumber)
        assertEquals("DKK", authorizeEvent.currency)
        assertEquals("20221123", authorizeEvent.date)
        assertEquals("1", authorizeEvent.eci)
        assertEquals("2", authorizeEvent.expireMonth)
        assertEquals("25", authorizeEvent.expireYear)
        assertEquals("54321", authorizeEvent.feeId)
        assertEquals("a1b23c4d56789e0fg1h234567890ijk1", authorizeEvent.hash)
        assertEquals("DNK", authorizeEvent.issuerCountry)
        assertEquals("1234", authorizeEvent.orderId)
        assertEquals("2", authorizeEvent.paymentType)
        assertEquals("987654321098", authorizeEvent.reference)
        assertEquals("123456", authorizeEvent.subscriptionId)
        assertEquals("0934", authorizeEvent.time)
        assertEquals("a1b2c3d4e5f6", authorizeEvent.tokenId)
        assertEquals("0", authorizeEvent.txnFee)
        assertEquals("123456789012345678", authorizeEvent.txnId)
        assertEquals("inline", authorizeEvent.ui)
        assertEquals("MobilePay", authorizeEvent.walletName)
        val expectedAdditionalFields = mapOf("extra1" to "extraValue1", "extra2" to "extraValue2")
        assertEquals(expectedAdditionalFields, authorizeEvent.additionalFields)
    }

    @Test
    fun decode_authorize_with_additional_fields_missing_nullable_fields_success() {
        val jsonResponse = """
        {
         "acceptUrl":"https://example.org/accept",
         "data": {
            "ui":"inline",
            "txnid":"123456789012345678",
            "orderid":"1234",
            "reference":"987654321098",
            "amount":"100",
            "currency":"DKK",
            "date":"20221123",
            "time":"0934",
            "feeid":"54321",
            "txnfee":"0",
            "paymenttype":"2",
            "hash":"a1b23c4d56789e0fg1h234567890ijk1",
            "extra1":"extraValue1",
            "extra2":"extraValue2"
          }
        }
        """

        val event = CheckoutEventMapper.mapEventObject(EventType.AUTHORIZE, jsonResponse)

        assertTrue(event is Event.Authorize)

        val authorizeEvent = event as Event.Authorize

        assertEquals("100", authorizeEvent.amount)
        assertNull(authorizeEvent.cardNumber)
        assertEquals("DKK", authorizeEvent.currency)
        assertEquals("20221123", authorizeEvent.date)
        assertNull(authorizeEvent.eci)
        assertNull(authorizeEvent.expireMonth)
        assertNull(authorizeEvent.expireYear)
        assertEquals("54321", authorizeEvent.feeId)
        assertEquals("a1b23c4d56789e0fg1h234567890ijk1", authorizeEvent.hash)
        assertNull(authorizeEvent.issuerCountry)
        assertEquals("1234", authorizeEvent.orderId)
        assertEquals("2", authorizeEvent.paymentType)
        assertEquals("987654321098", authorizeEvent.reference)
        assertNull(authorizeEvent.subscriptionId)
        assertEquals("0934", authorizeEvent.time)
        assertNull(authorizeEvent.tokenId)
        assertEquals("0", authorizeEvent.txnFee)
        assertEquals("123456789012345678", authorizeEvent.txnId)
        assertEquals("inline", authorizeEvent.ui)
        assertNull(authorizeEvent.walletName)
        val expectedAdditionalFields = mapOf("extra1" to "extraValue1", "extra2" to "extraValue2")
        assertEquals(expectedAdditionalFields, authorizeEvent.additionalFields)
    }

    @Test
    fun decode_authorize_with_additional_fields_missing_nonnullable_field_failed() {
        val jsonResponse = """
        {
         "acceptUrl":"https://example.org/accept",
         "data": {
            "ui":"inline",
            "txnid":"123456789012345678",
            "reference":"987654321098",
            "amount":"100",
            "currency":"DKK",
            "date":"20221123",
            "time":"0934",
            "subscriptionid":"123456",
            "feeid":"54321",
            "txnfee":"0",
            "expmonth":"2",
            "expyear":"25",
            "paymenttype":"2",
            "cardno":"123456XXXXXX1234",
            "eci":"1",
            "issuercountry":"DNK",
            "tokenid":"a1b2c3d4e5f6",
            "hash":"a1b23c4d56789e0fg1h234567890ijk1",
            "walletname":"MobilePay",
            "extra1":"extraValue1",
            "extra2":"extraValue2"
          }
        }
        """

        assertFailsWith<BamboraException.GenericException> {
            val event = CheckoutEventMapper.mapEventObject(EventType.AUTHORIZE, jsonResponse)
        }
    }
}