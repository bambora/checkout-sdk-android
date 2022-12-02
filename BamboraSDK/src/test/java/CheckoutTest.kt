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

import com.bambora.android.java.bamborasdk.*
import junit.framework.TestCase.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

internal class CheckoutTest {
    private val mockBamboraCheckoutActivity = mock(BamboraCheckoutActivity::class.java)
    private lateinit var defaultCheckout: Checkout
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
        defaultCheckout = Bambora.checkout("sessionTokenReturlUrlCorrect", "appSchemeTest")
        defaultCheckout.bamboraCheckoutActivity = mockBamboraCheckoutActivity
    }

    @After
    fun closeSDK() {
        Bambora.close()
    }

    @Test
    fun returnUrl_correct() {
        assertEquals("appSchemeTest://bamborasdk/return", defaultCheckout.returnUrl)
    }

    @Test
    fun baseUrl_empty() {
        val defaultBaseUrl = "https://v1.checkout.bambora.com"

        assertEquals(defaultBaseUrl, defaultCheckout.baseUrl)
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
        assertTrue(defaultCheckout.subscribedEvents.containsAll(listOf(EventType.AUTHORIZE, EventType.PAYMENT_TYPE_SELECTION, EventType.CARD_TYPE_RESOLVE, EventType.ERROR)))
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
        assertTrue(defaultCheckout.subscribedEvents.containsAll(listOf(EventType.AUTHORIZE, EventType.PAYMENT_TYPE_SELECTION, EventType.CARD_TYPE_RESOLVE)))
    }

    @Test
    fun unsubscribe_from_single_event() {
        defaultCheckout.on(eventsToSubscribe)

        assertEquals(3, defaultCheckout.subscribedEvents.count())

        defaultCheckout.off(EventType.PAYMENT_TYPE_SELECTION)

        assertEquals(2, defaultCheckout.subscribedEvents.count())
        assertTrue(defaultCheckout.subscribedEvents.containsAll(listOf(EventType.AUTHORIZE, EventType.CARD_TYPE_RESOLVE)))
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
        defaultCheckout.on(listOf(EventType.AUTHORIZE, EventType.CARD_TYPE_RESOLVE, EventType.AUTHORIZE))

        assertEquals(2, defaultCheckout.subscribedEvents.count())
        assertTrue(defaultCheckout.subscribedEvents.containsAll(listOf(EventType.AUTHORIZE, EventType.CARD_TYPE_RESOLVE)))
    }
}