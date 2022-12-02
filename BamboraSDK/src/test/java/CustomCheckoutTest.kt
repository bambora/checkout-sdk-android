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

import com.bambora.android.java.bamborasdk.Bambora
import com.bambora.android.java.bamborasdk.BamboraCheckoutActivity
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Test
import org.mockito.Mockito

internal class CustomCheckoutTest {
    private val mockBamboraCheckoutActivity = Mockito.mock(BamboraCheckoutActivity::class.java)

    @After
    fun closeSDK() {
        Bambora.close()
    }


    @Test
    fun baseUrl_with_value() {
        val customBaseUrl = "https://base.url.com"
        val checkout = Bambora.checkout("sessionTokenBaseUrlWithValue", "appSchemeTest", customBaseUrl)
        checkout.bamboraCheckoutActivity = mockBamboraCheckoutActivity

        assertEquals(customBaseUrl, checkout.baseUrl)
    }
}