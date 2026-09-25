# BetterMobileAds development guide

This document describes how to develop, configure, test, and release BetterMobileAds in an Expo React Native application.

## 1. Install and generate native projects

Install the package in an Expo app:

```sh
npx expo install better-mobile-ads
```

Add the config plugin to `app.json` or `app.config.js`. Use the application ID from the AdMob console; the test value below is only for local development.

```json
{
  "expo": {
    "plugins": [
      [
        "better-mobile-ads",
        {
          "androidAppId": "ca-app-pub-3940256099942544~3347511713",
          "iosAppId": "ca-app-pub-3940256099942544~1458002511",
          "iosSkAdNetworkItems": ["cstr6suwn9.skadnetwork"]
        }
      ]
    ]
  }
}
```

`androidAppId` writes the Google Mobile Ads application metadata to `AndroidManifest.xml`. `iosAppId` writes `GADApplicationIdentifier` to `Info.plist`; configure it separately because Android and iOS application IDs are not interchangeable. `iosSkAdNetworkItems` writes `SKAdNetworkItems`; keep this list aligned with Google's current mediation and SKAdNetwork documentation rather than copying an old list into a long-lived app.

Regenerate native projects after changing plugin options:

```sh
npx expo prebuild
npx pod-install
npx expo run:android
npx expo run:ios
```

iOS builds require macOS/Xcode. The package's iOS podspec pulls `Google-Mobile-Ads-SDK` and requires iOS 16.4 or newer.

## 2. Initialize the SDK

Initialize once at application startup, after the app has completed any required consent flow:

```js
import BetterMobileAds from "better-mobile-ads";

await BetterMobileAds.initialize({
  interstitialAdUnitId: __DEV__
    ? "ca-app-pub-3940256099942544/1033173712"
    : process.env.EXPO_PUBLIC_ADMOB_INTERSTITIAL_ID,
  rewardedAdUnitId: __DEV__
    ? "ca-app-pub-3940256099942544/5224354917"
    : process.env.EXPO_PUBLIC_ADMOB_REWARDED_ID,
  testMode: __DEV__
});
```

At least one ad unit ID is required. `testMode` supplies Google's test units only when an ID for that format is omitted. Never ship `testMode: true` or Google's test units in a production build. Keep real IDs out of source control and inject them through the app's build-time configuration.

The SDK initialization promise resolves after the native SDK reports initialization. Call `getVersion()` to record the native SDK version in diagnostics.

## 3. Load and show ads

Ads should be loaded before the user reaches the point where they are needed. Loading is single-slot: a later successful load replaces the prior loaded ad.

```js
await BetterMobileAds.loadInterstitial();
// Later, after a user action:
await BetterMobileAds.showInterstitial();

await BetterMobileAds.loadRewarded();
await BetterMobileAds.showRewarded();
```

`showInterstitial` and `showRewarded` reject with `ERR_AD_NOT_LOADED` when there is no ready ad and `ERR_VIEW_CONTROLLER_NULL`/`ERR_ACTIVITY_NULL` when the app is not currently able to present one. A displayed ad is consumed; load another ad after dismissal.

Rewarded ads emit the reward only after Google confirms that the user earned it. Do not grant a reward merely because `showRewarded()` resolved.

## 4. Events

The native module emits:

| Event | Payload | Meaning |
| --- | --- | --- |
| `onRewarded` | `{ type: string, amount: number }` | Google awarded the user a reward |
| `onAdDismissed` | `{ type: "interstitial" \| "rewarded" }` | Full-screen content was dismissed |
| `onAdFailedToLoad` | `{ type: string, error: string }` | Loading failed |
| `onAdFailedToShow` | `{ type: string, error: string }` | Full-screen presentation failed |

Use the Expo module event-listener API exposed by the native module in the target app, and remove subscriptions when the component unmounts. The package intentionally does not turn a failed ad into a fake success.

## 5. Production and store compliance

Before release:

1. Integrate Google's UMP SDK and request/update consent before loading ads where required.
2. On iOS, request ATT authorization at the appropriate point in the user experience and include the required `NSUserTrackingUsageDescription`.
3. Declare accurate privacy/data-safety disclosures in App Store Connect and Google Play Console.
4. Configure child-directed treatment, under-age-of-consent treatment, and ad personalization according to the audience and applicable law.
5. Use production ad unit IDs only in release builds. Keep test devices and test mode enabled during QA.
6. Configure the current SKAdNetwork identifiers required by the Google SDK and any mediation partners.
7. Do not show interstitials at app launch, during navigation, or immediately after another interstitial. Trigger them at natural breaks and respect frequency limits.
8. Do not incentivize invalid clicks, obscure close controls, or grant rewards for anything other than a confirmed rewarded callback.

This wrapper handles SDK initialization, ad loading, presentation, dismissal, and reward callbacks. Consent, ATT, targeting flags, analytics policy, and store declarations remain application responsibilities.

## 6. Developing this package

From the repository root:

```sh
npm install
npm run lint
npm run build
npm test -- --runInBand
```

The package has no simulator-independent native ad test suite. JavaScript/config-plugin tests should mock `expo-modules-core`; native behavior must be verified in a development build on Android and iOS with Google's test ad units.

Android source is in `android/src/main/java/com/soleroks/bettermobileads/BetterMobileAdsModule.kt`. iOS source is in `ios/BetterMobileAdsModule.swift`. The Expo config plugin is `app.plugin.js`.

## 7. Troubleshooting

- **Application ID missing:** verify the plugin is listed, run `npx expo prebuild`, and inspect the generated manifest/Info.plist.
- **No ad loaded:** check initialization completed, the correct platform ad unit is configured, consent allows a request, and the device has network access.
- **iOS presentation failure:** present only while the app has an active foreground scene; do not call show during a transition or while another controller is presented.
- **Android activity error:** call show while the app's foreground activity is available.
- **Real ads not serving during development:** use Google's test units and test devices. New production units can take time to fill.
