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

import android.net.Uri
import com.bambora.android.java.checkoutsdk.extensions.isDeeplink
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class DeeplinkTest {
    @Test
    fun uri_is_deeplink() {
        var deeplink = Uri.parse("mobilepay://app")
        var isDeeplink = deeplink.isDeeplink()

        assertTrue(isDeeplink)

        deeplink = Uri.parse("vipps://app")
        isDeeplink = deeplink.isDeeplink()

        assertTrue(isDeeplink)

        deeplink = Uri.parse("swish://app")
        isDeeplink = deeplink.isDeeplink()

        assertTrue(isDeeplink)
    }

    @Test
    fun uri_is_no_deeplink() {
        var link = Uri.parse("http://google.com")
        var isDeeplink = link.isDeeplink()

        assertFalse(isDeeplink)

        link = Uri.parse("https://google.com")
        isDeeplink = link.isDeeplink()

        assertFalse(isDeeplink)
    }

    @Test
    fun string_is_deeplink() {
        var deeplink = "mobilepay://app"
        var isDeeplink = deeplink.isDeeplink()

        assertTrue(isDeeplink)

        deeplink = "vipps://app"
        isDeeplink = deeplink.isDeeplink()

        assertTrue(isDeeplink)

        deeplink = "swish://app"
        isDeeplink = deeplink.isDeeplink()

        assertTrue(isDeeplink)
    }

    @Test
    fun string_is_no_deeplink() {
        var link = "http://google.com"
        var isDeeplink = link.isDeeplink()

        assertFalse(isDeeplink)

        link = "https://google.com"
        isDeeplink = link.isDeeplink()

        assertFalse(isDeeplink)
    }
}
