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

import android.content.Context
import com.bambora.android.java.checkoutsdk.Bambora
import com.bambora.android.java.checkoutsdk.BamboraCheckoutActivity
import com.bambora.android.java.checkoutsdk.extensions.isPackageInstalled
import io.mockk.every
import io.mockk.mockkStatic
import java.util.UUID
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class ConstructCheckoutUrlTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setupMockIsPackageInstalled() {
            mockkStatic("com.bambora.android.java.checkoutsdk.extensions.isPackageInstalled")
        }
    }

    private val mockBamboraCheckoutActivity = mock(BamboraCheckoutActivity::class.java)
    private val mockContext = mock(Context::class.java)
    private val customBaseUrl = "https://base.url.com"
    private val productionUrl = "https://v1.checkout.bambora.com"
    private val appScheme = "bamborademoapp"
    private val sessionToken = UUID.randomUUID().toString()

    @After
    fun closeSDK() {
        Bambora.close()
    }

    @Test
    fun construct_checkout_url_no_apps_installed_with_custom_base_url() {
        every {
            mockContext.isPackageInstalled("dk.danskebank.mobilepay")
        } returns false
        every {
            mockContext.isPackageInstalled("no.dnb.vipps")
        } returns false
        every {
            mockContext.isPackageInstalled("se.bankgirot.swish")
        } returns false

        val checkout = Bambora.checkout(sessionToken, appScheme, mockContext, customBaseUrl)
        checkout.bamboraCheckoutActivity = mockBamboraCheckoutActivity

        val expectedEncodedPaymentOptions =
            """
            eyJhcHBSZXR1cm5VcmwiOiJiYW1ib3JhZGVtb2FwcDovL2JhbWJvcmFzZGsvcmV0dXJuIiwicGF5bWVudEFwcHNJbnN0YWxsZWQiOltdLCJ2ZXJzaW9uIjoiQ2hlY2tvdXRTREtBbmRyb2lkLzIuMC4wIn0=
            """.trimIndent()
        val expectedCheckoutUrl =
            "$customBaseUrl/$sessionToken?ui=inline#$expectedEncodedPaymentOptions"

        assertEquals(expectedCheckoutUrl, checkout.checkoutUrl)
    }

    @Test
    fun construct_checkout_url_mobilepay_installed_with_production_url() {
        every {
            mockContext.isPackageInstalled("dk.danskebank.mobilepay")
        } returns true
        every {
            mockContext.isPackageInstalled("no.dnb.vipps")
        } returns false
        every {
            mockContext.isPackageInstalled("se.bankgirot.swish")
        } returns false

        val checkout = Bambora.checkout(sessionToken, appScheme, mockContext)
        checkout.bamboraCheckoutActivity = mockBamboraCheckoutActivity

        val expectedEncodedPaymentOptions =
            """
            eyJhcHBSZXR1cm5VcmwiOiJiYW1ib3JhZGVtb2FwcDovL2JhbWJvcmFzZGsvcmV0dXJuIiwicGF5bWVudEFwcHNJbnN0YWxsZWQiOlsibW9iaWxlcGF5Il0sInZlcnNpb24iOiJDaGVja291dFNES0FuZHJvaWQvMi4wLjAifQ==
            """.trimIndent()
        val expectedCheckoutUrl =
            "$productionUrl/$sessionToken?ui=inline#$expectedEncodedPaymentOptions"

        assertEquals(expectedCheckoutUrl, checkout.checkoutUrl)
    }

    @Test
    fun construct_checkout_url_vipps_installed_with_production_url() {
        every {
            mockContext.isPackageInstalled("dk.danskebank.mobilepay")
        } returns false
        every {
            mockContext.isPackageInstalled("no.dnb.vipps")
        } returns true
        every {
            mockContext.isPackageInstalled("se.bankgirot.swish")
        } returns false

        val checkout = Bambora.checkout(sessionToken, appScheme, mockContext)
        checkout.bamboraCheckoutActivity = mockBamboraCheckoutActivity

        val expectedEncodedPaymentOptions =
            """
            eyJhcHBSZXR1cm5VcmwiOiJiYW1ib3JhZGVtb2FwcDovL2JhbWJvcmFzZGsvcmV0dXJuIiwicGF5bWVudEFwcHNJbnN0YWxsZWQiOlsidmlwcHMiXSwidmVyc2lvbiI6IkNoZWNrb3V0U0RLQW5kcm9pZC8yLjAuMCJ9
            """.trimIndent()
        val expectedCheckoutUrl =
            "$productionUrl/$sessionToken?ui=inline#$expectedEncodedPaymentOptions"

        assertEquals(expectedCheckoutUrl, checkout.checkoutUrl)
    }

    @Test
    fun construct_checkout_url_swish_installed_with_production_url() {
        every {
            mockContext.isPackageInstalled("dk.danskebank.mobilepay")
        } returns false
        every {
            mockContext.isPackageInstalled("no.dnb.vipps")
        } returns false
        every {
            mockContext.isPackageInstalled("se.bankgirot.swish")
        } returns true

        val checkout = Bambora.checkout(sessionToken, appScheme, mockContext)
        checkout.bamboraCheckoutActivity = mockBamboraCheckoutActivity

        val expectedEncodedPaymentOptions =
            """
            eyJhcHBSZXR1cm5VcmwiOiJiYW1ib3JhZGVtb2FwcDovL2JhbWJvcmFzZGsvcmV0dXJuIiwicGF5bWVudEFwcHNJbnN0YWxsZWQiOlsic3dpc2giXSwidmVyc2lvbiI6IkNoZWNrb3V0U0RLQW5kcm9pZC8yLjAuMCJ9
            """.trimIndent()
        val expectedCheckoutUrl =
            "$productionUrl/$sessionToken?ui=inline#$expectedEncodedPaymentOptions"

        assertEquals(expectedCheckoutUrl, checkout.checkoutUrl)
    }

    @Test
    fun construct_checkout_url_all_apps_installed_with_production_url() {
        every {
            mockContext.isPackageInstalled("dk.danskebank.mobilepay")
        } returns true
        every {
            mockContext.isPackageInstalled("no.dnb.vipps")
        } returns true
        every {
            mockContext.isPackageInstalled("se.bankgirot.swish")
        } returns true

        val checkout = Bambora.checkout(sessionToken, appScheme, mockContext)
        checkout.bamboraCheckoutActivity = mockBamboraCheckoutActivity

        val expectedEncodedPaymentOptions =
            """
            eyJhcHBSZXR1cm5VcmwiOiJiYW1ib3JhZGVtb2FwcDovL2JhbWJvcmFzZGsvcmV0dXJuIiwicGF5bWVudEFwcHNJbnN0YWxsZWQiOlsibW9iaWxlcGF5IiwidmlwcHMiLCJzd2lzaCJdLCJ2ZXJzaW9uIjoiQ2hlY2tvdXRTREtBbmRyb2lkLzIuMC4wIn0=
            """.trimIndent()
        val expectedCheckoutUrl =
            "$productionUrl/$sessionToken?ui=inline#$expectedEncodedPaymentOptions"

        assertEquals(expectedCheckoutUrl, checkout.checkoutUrl)
    }
}
