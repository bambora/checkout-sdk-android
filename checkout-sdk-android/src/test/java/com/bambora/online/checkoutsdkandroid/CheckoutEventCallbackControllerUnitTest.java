/*
 * Copyright (c) 2018 Bambora ( https://bambora.com/ )
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

package com.bambora.online.checkoutsdkandroid;

import com.bambora.online.checkoutsdkandroid.controllers.CheckoutEventCallbackController;
import com.bambora.online.checkoutsdkandroid.interfaces.ICheckoutEventCallback;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CheckoutEventCallbackControllerUnitTest {

    @Test
    public void addMultipleEvents()
    {
        //Arrange
        String authorizeEvent = "Authorize";
        String closeEvent = "Close";
        int expectedMapSize = 2;
        int expectedAuthorizeArraySize = 2;
        int expectedCloseArraySize = 1;
        CheckoutEventCallbackController controller = new CheckoutEventCallbackController();
        controller.on(authorizeEvent, new ICheckoutEventCallback() {
            @Override
            public void onEventDispatched(String eventType, String jsonPayload) {}
        });
        controller.on(authorizeEvent, new ICheckoutEventCallback() {
            @Override
            public void onEventDispatched(String eventType, String jsonPayload) {}
        });
        controller.on(closeEvent, new ICheckoutEventCallback() {
            @Override
            public void onEventDispatched(String eventType, String jsonPayload) {}
        });
        //Act
        Map<String, ArrayList<ICheckoutEventCallback>> events = controller.getCallbackEventCollection();
        int actualAuthorizeEventSize = controller.getEventCallback(authorizeEvent).size();
        int actualCloseEventSize = controller.getEventCallback(closeEvent).size();
        //Assert
        assertEquals(expectedMapSize, events.size());
        assertEquals(expectedAuthorizeArraySize, actualAuthorizeEventSize);
        assertEquals(expectedCloseArraySize, actualCloseEventSize);
    }

    @Test
    public void dispatchEvent()
    {
        //Arrange
        final String expectedEventType = "Authorize";
        final String expectedPayload = "expectedPayload";
        CheckoutEventCallbackController controller = new CheckoutEventCallbackController();
        controller.on(expectedEventType, new ICheckoutEventCallback() {
            @Override
            public void onEventDispatched(String eventType, String jsonPayload) {
                //Assert
                assertEquals(expectedEventType.toLowerCase(), eventType);
                assertEquals(expectedPayload, jsonPayload);
            }
        });
        controller.on("Close", new ICheckoutEventCallback() {
            @Override
            public void onEventDispatched(String eventType, String jsonPayload) {
                //This should fail the test if the event is called.
                assertTrue(false);
            }
        });
        controller.on("*", new ICheckoutEventCallback() {
            @Override
            public void onEventDispatched(String eventType, String jsonPayload) {
                assertEquals(expectedEventType.toLowerCase(), eventType);
                assertEquals(expectedPayload, jsonPayload);
            }
        });
        //Act
        controller.dispatchEvent(expectedEventType, expectedPayload);
    }
}