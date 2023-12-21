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

import com.bambora.android.java.checkoutsdk.BamboraException
import com.bambora.android.java.checkoutsdk.extensions.processDeeplink
import junit.framework.TestCase.assertEquals
import kotlin.test.assertFailsWith
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class ReturnUrlTest {
    @Test
    fun get_returnurl_success() {
        val deeplink =
            """
            bamborademoapp://bamborasdk/return/return?epayreturn=https://wallet-v1.api-eu.bambora.com/allowed/domain
            """.trimIndent()
        val returnUrl = deeplink.processDeeplink()

        assertEquals("https://wallet-v1.api-eu.bambora.com/allowed/domain", returnUrl)
    }

    @Test
    fun get_returnurl_fail() {
        var deeplink = "bamborademoapp://bamborasdk/return?epayreturn=https://example.com"

        assertFailsWith<BamboraException.GenericException> {
            deeplink.processDeeplink()
        }

        deeplink = "bamborademoapp://bamborasdk/return/return?epayreturn/wrongvalue"
        assertFailsWith<BamboraException.GenericException> {
            deeplink.processDeeplink()
        }
    }
}
