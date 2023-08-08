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
import com.bambora.android.java.checkoutsdk.Checkout
import com.bambora.android.java.checkoutsdk.EventType
import com.bambora.android.java.checkoutsdk.extensions.isPackageInstalled
import io.mockk.every
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class CheckoutTest {
    companion object {
        private val mockContext = mock(Context::class.java)

        @BeforeClass
        @JvmStatic
        fun setupMockIsPackageInstalled() {
            mockkStatic("com.bambora.android.java.checkoutsdk.extensions.isPackageInstalled")
            every {
                mockContext.isPackageInstalled("dk.danskebank.mobilepay")
            } returns false
            every {
                mockContext.isPackageInstalled("no.dnb.vipps")
            } returns false
            every {
                mockContext.isPackageInstalled("se.bankgirot.swish")
            } returns false
        }
    }

    private val mockBamboraCheckoutActivity = mock(BamboraCheckoutActivity::class.java)
    private lateinit var defaultCheckout: Checkout
    private val appScheme = "appScheme"
    private val sessionToken = "369b2892a3c44a5699d557a37c4a78a6"
    private val returnUrl = "appScheme://bamborasdk/return"
    private val eventsToSubscribe = listOf(
        EventType.AUTHORIZE,
        EventType.PAYMENT_TYPE_SELECTION,
        EventType.CARD_TYPE_RESOLVE
    )
    private val eventsToUnsubscribe = listOf(
        EventType.PAYMENT_TYPE_SELECTION,
        EventType.CARD_TYPE_RESOLVE
    )

    @Before
    fun initDefaultCheckout() {
        defaultCheckout = Bambora.checkout(sessionToken, appScheme, mockContext)
        defaultCheckout.bamboraCheckoutActivity = mockBamboraCheckoutActivity
    }

    @After
    fun closeSDK() {
        Bambora.close()
    }

    @Test
    fun returnUrl_correct() {
        assertEquals(returnUrl, defaultCheckout.returnUrl)
    }

    @Test
    fun close_SDK() {
        Bambora.close()

        assertFalse(Bambora.isInitialized)
    }

    @Test
    fun subscribe_to_all_events() {
        defaultCheckout.subscribeOnAllEvents()

        assertEquals(4, defaultCheckout.subscribedEvents.count())
        assertTrue(
            defaultCheckout.subscribedEvents.containsAll(
                listOf(
                    EventType.AUTHORIZE,
                    EventType.PAYMENT_TYPE_SELECTION,
                    EventType.CARD_TYPE_RESOLVE,
                    EventType.ERROR
                )
            )
        )
    }

    @Test
    fun subscribe_to_single_event() {
        defaultCheckout.on(EventType.AUTHORIZE)

        assertEquals(1, defaultCheckout.subscribedEvents.count())
        assertTrue(defaultCheckout.subscribedEvents.contains(EventType.AUTHORIZE))
    }

    @Test
    fun subscribe_to_multiple_events() {
        defaultCheckout.on(eventsToSubscribe)

        assertEquals(3, defaultCheckout.subscribedEvents.count())
        assertTrue(
            defaultCheckout.subscribedEvents.containsAll(
                listOf(
                    EventType.AUTHORIZE,
                    EventType.PAYMENT_TYPE_SELECTION,
                    EventType.CARD_TYPE_RESOLVE
                )
            )
        )
    }

    @Test
    fun unsubscribe_from_single_event() {
        defaultCheckout.on(eventsToSubscribe)

        assertEquals(3, defaultCheckout.subscribedEvents.count())

        defaultCheckout.off(EventType.PAYMENT_TYPE_SELECTION)

        assertEquals(2, defaultCheckout.subscribedEvents.count())
        assertTrue(
            defaultCheckout.subscribedEvents.containsAll(
                listOf(EventType.AUTHORIZE, EventType.CARD_TYPE_RESOLVE)
            )
        )
    }

    @Test
    fun unsubscribe_from_multiple_events() {
        defaultCheckout.on(eventsToSubscribe)

        assertEquals(3, defaultCheckout.subscribedEvents.count())

        defaultCheckout.off(eventsToUnsubscribe)

        assertEquals(1, defaultCheckout.subscribedEvents.count())
        assertTrue(defaultCheckout.subscribedEvents.contains(EventType.AUTHORIZE))
    }

    @Test
    fun subscribe_to_same_event_twice() {
        defaultCheckout.on(
            listOf(EventType.AUTHORIZE, EventType.CARD_TYPE_RESOLVE, EventType.AUTHORIZE)
        )

        assertEquals(2, defaultCheckout.subscribedEvents.count())
        assertTrue(
            defaultCheckout.subscribedEvents.containsAll(
                listOf(EventType.AUTHORIZE, EventType.CARD_TYPE_RESOLVE)
            )
        )
    }
}
