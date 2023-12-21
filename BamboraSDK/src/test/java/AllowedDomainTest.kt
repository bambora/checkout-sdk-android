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

import com.bambora.android.java.checkoutsdk.extensions.isAllowedDomain
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AllowedDomainTest {

    @Test
    fun allowed_domain() {
        var allowedDomain = "https://wallet-v1.api-eu.bambora.com/allowed/domain"
        var isAllowed = allowedDomain.isAllowedDomain()

        assertTrue(isAllowed)

        allowedDomain = "https://authorize-v1.api-eu.bambora.com/allowed/domain"
        isAllowed = allowedDomain.isAllowedDomain()

        assertTrue(isAllowed)
    }

    @Test
    fun forbidden_domain() {
        var forbiddenDomain = "https://example.com/forbidden/domain"
        var isAllowed = forbiddenDomain.isAllowedDomain()

        assertFalse(isAllowed)

        forbiddenDomain = "https://forbidden.domain/forbidden/domain"
        isAllowed = forbiddenDomain.isAllowedDomain()

        assertFalse(isAllowed)
    }
}
