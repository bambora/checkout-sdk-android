# Bambora Checkout SDK for Android
The Checkout SDK for Android provides the tools necessary for integrating Bambora Checkout with your Android application. The SDK helps with displaying the Checkout session, and sending out events during the payment flow.

![Demo of the card payment flow.](/Assets/Card_Payment_Demo.gif)

## Supported Android versions
Minimal supported API level: 26 (Android 8).

## Installation
In order to use the Bambora Checkout SDK in your project, simply include it as a dependency.

### Gradle
The Checkout SDK is stored in the [Maven Central Repository](https://central.sonatype.com/artifact/com.bambora.checkout/androidsdk). To use dependencies from Maven Central in your project, you need to list `mavenCentral` as a repository in your project's `settings.gradle`.
```java
pluginManagement {
    repositories {
        mavenCentral()
    }
}
```

Then add the SDK as a dependency in your project's `build.gradle` file.
`implementation com.bambora.checkout:androidsdk:2.0.1`

## Usage
Processing a payment through the Bambora Checkout SDK requires only a few easy steps:

1. Creating a Checkout Session
2. Initializing the SDK
3. Showing the payment screen to your customer
4. Receiving events
5. Configuring 3rd party returns

### Creating a Checkout Session
To initialize the SDK, you need a session token that can be obtained by creating a Checkout session. For details on how to create a Checkout session, have a look at the [Bambora Development Documentation](https://developer.bambora.com/europe/checkout/getting-started/create-payment).

### Initializing the SDK
Initialize the SDK like so:
```java
val checkout = Bambora.checkout(sessionToken, appScheme, context)
```

Parameters:
- `sessionToken`: Token that you received in the previous step, when creating a session
- `appScheme`: The scheme name for your activity that is configured as intent-filter. How to setup the URL Scheme and intent-filter activity is explained in step 4, [3rd party returns](#3rd-party-returns)
- `context`: Activity in which the checkout is initialized. This context will be used to check which wallet apps are installed on the customer's device
- (Optional) `customUrl`: An option to override the default URL to which the SDK will connect

### Showing the payment screen to your customer
After having initialized the SDK, the Bambora Checkout UI can be displayed to your customer. To show the payment screen, simply call:
`checkout.show(context)`.

Parameter:
- `context`: Activity context that will host the Bambora Checkout payment sheet.

The SDK will render the Checkout web view in a pop up over the current screen. If the customer selects to pay with their wallet payment application, the SDK will make sure to open the corresponding app. The SDK will also handle the redirect back to the configured URL scheme.

### Subscribing to events
The SDK is event-driven. This means that it will sent out events when something notable occurs during the payment flow. To be able to receive these events, you'll have to subscribe to them. This can be done in three different ways.
- Subscribe to **all events**\
  Get notified when any event occurs:
  ```java
  checkout.subscribeOnAllEvents()
  ```

- Subscribe to **some events**\
  Get notified when specific events occur. Replace the events in the example below with the ones you need. The possible options are listed [here](#events-overview).
  ```java
  checkout.on(listOf(EventType.AUTHORIZE, EventType.CARD_TYPE_RESOLVE))
  ```
  Use the snippet below to subscribe to a single event.
   ```java
  checkout.on(EventType.AUTHORIZE)
  ```

- Unsubscribe from **some events**\
  Use the example below to unsubscribe from certain events. Replace the event types with the ones you no longer need to receive.
  ```java
  checkout.off(listOf(EventType.AUTHORIZE, EventType.CARD_TYPE_RESOLVE))
  ```
  Use the snippet below to unsubscribe from a specific event.
  ```java
  checkout.off(EventType.AUTHORIZE)
  ```

Instead of calling these functions on `checkout` each time, they can easily be applied at the same time on the same instance of the Checkout.
```java
Bambora.checkout(sessionToken, appScheme, context, customUrl).apply {
    subscribeOnAllEvents()
    // any other functions you want to call on the Checkout instance
}
```

### Receiving events
To handle the events that you have subscribed to, you need to assign an instance of `CheckoutEventReceiver` to your checkout's property `checkoutEventReceiver`. `CheckoutEventReceiver` needs to override the `onEventDispatched(event: Event)` function like below.
```java
checkout.checkoutEventReceiver = object : CheckoutEventReceiver {
    override fun onEventDispatched(event: Event) {
        if (event is Event.Authorize) {
            // do something when an Authorize event was intercepted
        } else {
            // do something when any other event was intercepted
        }
    }
}
```
Make sure to at least handle the `Authorize` event. This will tell you when the payment is completed. IMPORTANT: do not fulfil the order based on the status in this event alone. Verify with your server whether the payment was indeed completed.

#### Events overview
These are the events that can occur during the payment flow.
| Event                  | Trigger                                                                                                       | Data description |
| ---------------------- | ------------------------------------------------------------------------------------------------------------- | ----------------------------                                                 |
| Authorize              | Sent when a payment has been authorized                                                                       | Contains payment data, such as `txnId` and `orderId`                         |
| CheckoutViewClose      | Sent when the payment screen has been closed                                                                  | Contains no data                                                             |
| PaymentTypeSelection   | Sent when a payment type has been selected                                                                    | Contains the payment type, such as `paymentcard` or `mobilepay`              |
| CardTypeResolve        | Sent whenever enough digits of the payment card number have been entered to determine the payment card type   | Contains payment card type data, such as `id` and `fee`                      |
| ErrorEvent             | Sent when an error occurs                                                                                     | Contains a BamboraException type, such as `LoadSessionException` or `GenericException`  |

More information about the different types of Events and their data, can be found at the [Bambora Development Documentation](https://developer.bambora.com/europe/sdk/web-sdk/advanced-usage).

### 3rd party returns
Most of the payment flows that are supported by this SDK include a step that takes place in a 3rd party's app or webpage. Follow the setup in this section to make sure that your customers will automatically return to your app to continue their purchase. The SDK uses the Android deep link feature for this.

#### Android Manifest
Add an activity with the following configuration to your AndroidManifest.xml. This will register that activity as the receiver (intent-filter) for Deep Links that open your app.
```xml
<activity
  android:name=".ShoppingCartActivity"
  android:exported="true"
  android:launchMode="singleTask">
  <intent-filter>
    <action android:name="android.intent.action.VIEW" />

    <category android:name="android.intent.category.BROWSABLE" />
    <category android:name="android.intent.category.DEFAULT" />

    <data
     android:host="bamborasdk"
     android:path="/return"
     android:scheme="<your-app-scheme>" />
  </intent-filter>
</activity>
```

Make sure to configure a scheme name that includes your app name or brand, so that it is unique for your app. Note that if you already use deep links in your app for other functionalities, and don't want to re-use the scheme name, Android allows you to configure multiple intent-filter activities with different scheme names.

You are free to rename the activity (`ShoppingCartActivity` in this example), but please leave the host and path values untouched.

Provide the configured scheme name during initialization of the SDK:
```java
val checkout = Bambora.checkout(token, <your-app-scheme>, context)
```

#### Intent-filter Activity
In the configured intent-filter activity make sure that `onNewIntent(intent: Intent?)`, but also `onCreate(savedInstanceState: Bundle?)` are implemented and can deal with incoming intents. If your app is closed in the background while your customer is finishing the payment, `onCreate` will be called instead of `onNewIntent`. The SDK is designed to handle the incoming deep link url.
All you have to do is reinitialize the SDK by calling `BamboraSDK.checkoutAfterReturn(deeplink)`.
```java
class ShoppingCartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    internal val bamboraSDKHelper = BamboraSDKHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deeplink = intent?.dataString
        if (deeplink != null) {
            processDeeplink(deeplink)
        }

        // ...
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val deeplink = intent?.dataString
        if (deeplink != null) {
            processDeeplink(deeplink)
        }
    }

    fun processDeeplink(deeplink: String) {
        checkout = Bambora.checkoutAfterReturn(deeplink)

        // Provide the event receiver again to make sure it is set,
        // even if the app was closed during the payment.
        checkout.checkoutEventReceiver = myCheckoutEventReceiver
        checkout.subscribeOnAllEvents()
        checkout.show(this)
    }
}
```

## Closing the SDK
The Checkout SDK is closed automatically when a payment was authorized and therefore completed. In all other scenarios, the SDK can be closed by calling `Bambora.close()`. This is required before the SDK can be reinitialized for a new payment.

Note that, if the user is still completing the payment, this may cause the payment to fail.

When the SDK is closed, a few things happen:
- The Activity showing the Checkout web view is closed, and thus the web view is also dismissed immediately
- Resources of the SDK, such as Checkout, are cleaned up
- A new payment session can now be instantiated

## Demo App
The included Demo App shows how you can use the Bambora Checkout SDK in your own app. It shows how to implement the following features:
- Initialize a session with Bambora's default production URL
- Obtain the details of a payment after it was completed
- Listen and respond to events
- Configure the intent-filter
