import ExpoModulesCore
import GoogleMobileAds
import UIKit

public class BetterMobileAdsModule: Module, FullScreenContentDelegate {
  private var interstitialAd: InterstitialAd?
  private var rewardedAd: RewardedAd?
  private var interstitialAdUnitId: String?
  private var rewardedAdUnitId: String?

  public func definition() -> ModuleDefinition {
    Name("BetterMobileAds")

    Events("onRewarded", "onAdDismissed", "onAdFailedToLoad", "onAdFailedToShow")

    AsyncFunction("initialize") { (options: [String: Any]?, promise: Promise) in
      self.interstitialAdUnitId = options?["interstitialAdUnitId"] as? String
      self.rewardedAdUnitId = options?["rewardedAdUnitId"] as? String
      let testMode = options?["testMode"] as? Bool ?? false

      if testMode {
        self.interstitialAdUnitId = self.interstitialAdUnitId ?? "ca-app-pub-3940256099942544/1033173712"
        self.rewardedAdUnitId = self.rewardedAdUnitId ?? "ca-app-pub-3940256099942544/1712485313"
      }

      guard self.interstitialAdUnitId != nil || self.rewardedAdUnitId != nil else {
        promise.reject("ERR_CONFIG_INVALID", "At least one ad unit ID is required. Use testMode only for development.", nil)
        return
      }

      DispatchQueue.main.async {
        MobileAds.shared.start { _ in
          promise.resolve(true)
        }
      }
    }

    Function("getVersion") {
      MobileAds.shared.version
    }

    AsyncFunction("loadInterstitial") { (promise: Promise) in
      guard let adUnitId = self.interstitialAdUnitId else {
        promise.reject("ERR_NOT_INITIALIZED", "Initialize with an interstitial ad unit ID first.", nil)
        return
      }
      DispatchQueue.main.async {
        InterstitialAd.load(with: adUnitId, request: Request()) { ad, error in
          if let error {
            self.interstitialAd = nil
            self.sendEvent("onAdFailedToLoad", ["type": "interstitial", "error": error.localizedDescription])
            promise.reject("ERR_AD_FAILED", error.localizedDescription, error)
            return
          }
          self.interstitialAd = ad
          self.interstitialAd?.fullScreenContentDelegate = self
          promise.resolve(true)
        }
      }
    }

    AsyncFunction("showInterstitial") { (promise: Promise) in
      guard let ad = self.interstitialAd else {
        promise.reject("ERR_AD_NOT_LOADED", "Interstitial ad is not loaded.", nil)
        return
      }
      guard let viewController = self.presentingViewController() else {
        promise.reject("ERR_VIEW_CONTROLLER_NULL", "No view controller is available to present the ad.", nil)
        return
      }
      self.interstitialAd = nil
      DispatchQueue.main.async {
        ad.present(from: viewController)
        promise.resolve(true)
      }
    }

    AsyncFunction("loadRewarded") { (promise: Promise) in
      guard let adUnitId = self.rewardedAdUnitId else {
        promise.reject("ERR_NOT_INITIALIZED", "Initialize with a rewarded ad unit ID first.", nil)
        return
      }
      DispatchQueue.main.async {
        RewardedAd.load(with: adUnitId, request: Request()) { ad, error in
          if let error {
            self.rewardedAd = nil
            self.sendEvent("onAdFailedToLoad", ["type": "rewarded", "error": error.localizedDescription])
            promise.reject("ERR_AD_FAILED", error.localizedDescription, error)
            return
          }
          self.rewardedAd = ad
          self.rewardedAd?.fullScreenContentDelegate = self
          promise.resolve(true)
        }
      }
    }

    AsyncFunction("showRewarded") { (promise: Promise) in
      guard let ad = self.rewardedAd else {
        promise.reject("ERR_AD_NOT_LOADED", "Rewarded ad is not loaded.", nil)
        return
      }
      guard let viewController = self.presentingViewController() else {
        promise.reject("ERR_VIEW_CONTROLLER_NULL", "No view controller is available to present the ad.", nil)
        return
      }
      self.rewardedAd = nil
      DispatchQueue.main.async {
        ad.present(from: viewController) {
          let reward = ad.adReward
          self.sendEvent("onRewarded", ["type": reward.type, "amount": reward.amount])
        }
        promise.resolve(true)
      }
    }
  }

  public func adDidDismissFullScreenContent(_ ad: FullScreenPresentingAd) {
    sendEvent("onAdDismissed", ["type": ad is RewardedAd ? "rewarded" : "interstitial"])
  }

  public func ad(_ ad: FullScreenPresentingAd, didFailToPresentFullScreenContentWithError error: Error) {
    sendEvent("onAdFailedToShow", ["type": ad is RewardedAd ? "rewarded" : "interstitial", "error": error.localizedDescription])
  }

  private func presentingViewController() -> UIViewController? {
    guard let windowScene = UIApplication.shared.connectedScenes
      .compactMap({ $0 as? UIWindowScene })
      .first(where: { $0.activationState == .foregroundActive }) else {
      return nil
    }
    var viewController = windowScene.windows.first(where: { $0.isKeyWindow })?.rootViewController
    while let presented = viewController?.presentedViewController {
      viewController = presented
    }
    return viewController
  }
}
