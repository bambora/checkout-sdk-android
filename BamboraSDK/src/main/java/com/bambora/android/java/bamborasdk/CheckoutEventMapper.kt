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

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 *  Maps events to [Event] and [EventType].
 */
internal object CheckoutEventMapper {

    /**
     * Maps String to [EventType].
     *
     * @param eventType String that can be mapped to [EventType].
     *
     * @return instance of [EventType]. If eventType cannot be mapped to an [EventType], it will return [EventType.ERROR].
     */
    fun mapEventType(eventType: String): EventType {
        return when (eventType) {
            "paymentTypeSelection" -> EventType.PAYMENT_TYPE_SELECTION
            "cardTypeResolve" -> EventType.CARD_TYPE_RESOLVE
            "authorize" -> EventType.AUTHORIZE

            else -> EventType.ERROR
        }
    }

    /**
     *  Maps [EventType] and String to [Event].
     *  Deserializes jsonPayload to [Event] body when available.
     *
     *  @param eventType instance of [EventType].
     *  @param jsonPayload payload for [EventType].
     *
     *  @return instance of [Event]. If eventType cannot be mapped to an [Event], it will return [Event.Error].
     */
    fun mapEventObject(eventType: EventType, jsonPayload: String): Event {
        return when (eventType) {
            EventType.PAYMENT_TYPE_SELECTION -> Event.PaymentTypeSelection(jsonPayload.replace("\"",""))
            EventType.CARD_TYPE_RESOLVE -> Json.decodeFromString<Event.CardTypeResolve>(jsonPayload)
            EventType.AUTHORIZE -> Json.decodeFromString<AuthorizeResponse>(jsonPayload).data
            else -> Event.Error(BamboraException.GenericException)
        }
    }
}
