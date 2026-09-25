# BetterMobileAds

An Expo module wrapper for Google Mobile Ads interstitial and rewarded ads on Android and iOS.

See [DEVELOPMENT.md](./DEVELOPMENT.md) for installation, configuration, API usage, testing, release, and privacy/compliance guidance.

## Platform support

- Android: Google Play services Ads SDK
- iOS: Google Mobile Ads SDK
- Web: unsupported (methods reject with an explicit error)

The module does not replace Google's User Messaging Platform (UMP), App Tracking Transparency (ATT), or your app's consent and privacy implementation. Complete those flows before requesting ads in production.
