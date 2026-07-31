package com.soleroks.bettermobileads

import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class BetterMobileAdsModule : Module() {

  private var interstitialAd: InterstitialAd? = null
  private var rewardedAd: RewardedAd? = null

  override fun definition() = ModuleDefinition {

    Name("BetterMobileAds")

    Events("onRewarded", "onAdDismissed", "onAdFailedToLoad")

    Function("initialize") {
      val activity = appContext.activityProvider?.currentActivity ?: return@Function false

      Handler(Looper.getMainLooper()).post {
        MobileAds.initialize(activity)
      }
      true
    }

    Function("getVersion") {
      MobileAds.getVersion().toString()
    }


    AsyncFunction("loadInterstitial") { promise: Promise ->
      val activity = appContext.activityProvider?.currentActivity
      if (activity == null) {
        promise.reject("ERR_ACTIVITY_NULL", "Activity null", null)
        return@AsyncFunction
      }

      Handler(Looper.getMainLooper()).post {
        try {
          val adRequest = AdRequest.Builder().build()
          InterstitialAd.load(
            activity,
            "ca-app-pub-3940256099942544/1033173712", // Test Interstitial ID
            adRequest,
            object : InterstitialAdLoadCallback() {
              override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                promise.resolve(true)
              }

              override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
                sendEvent("onAdFailedToLoad", mapOf("type" to "interstitial", "error" to error.message))
                promise.reject("ERR_AD_FAILED", error.message, Exception(error.message))
              }
            }
          )
        } catch (e: Exception) {
          promise.reject("ERR_LOAD_EXCEPTION", e.localizedMessage, e)
        }
      }
    }

    AsyncFunction("showInterstitial") { promise: Promise ->
      val activity = appContext.activityProvider?.currentActivity
      if (activity == null) {
        promise.reject("ERR_ACTIVITY_NULL", "Activity null", null)
        return@AsyncFunction
      }

      val ad = interstitialAd
      if (ad == null) {
        promise.reject("ERR_AD_NOT_LOADED", "Interstitial ad yüklenmedi", null)
        return@AsyncFunction
      }

      Handler(Looper.getMainLooper()).post {
        try {
          ad.show(activity)
          interstitialAd = null
          promise.resolve(true)
        } catch (e: Exception) {
          promise.reject("ERR_SHOW_FAILED", e.localizedMessage, e)
        }
      }
    }


    AsyncFunction("loadRewarded") { promise: Promise ->
      val activity = appContext.activityProvider?.currentActivity
      if (activity == null) {
        promise.reject("ERR_ACTIVITY_NULL", "Activity null", null)
        return@AsyncFunction
      }

      Handler(Looper.getMainLooper()).post {
        try {
          val adRequest = AdRequest.Builder().build()
          RewardedAd.load(
            activity,
            "ca-app-pub-3940256099942544/5224354917", 
            adRequest,
            object : RewardedAdLoadCallback() {
              override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                promise.resolve(true)
              }

              override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedAd = null
                sendEvent("onAdFailedToLoad", mapOf("type" to "rewarded", "error" to error.message))
                promise.reject("ERR_AD_FAILED", error.message, Exception(error.message))
              }
            }
          )
        } catch (e: Exception) {
          promise.reject("ERR_LOAD_EXCEPTION", e.localizedMessage, e)
        }
      }
    }

    AsyncFunction("showRewarded") { promise: Promise ->
      val activity = appContext.activityProvider?.currentActivity
      if (activity == null) {
        promise.reject("ERR_ACTIVITY_NULL", "Activity null", null)
        return@AsyncFunction
      }

      val ad = rewardedAd
      if (ad == null) {
        promise.reject("ERR_AD_NOT_LOADED", "Rewarded ad yüklenmedi", null)
        return@AsyncFunction
      }

      Handler(Looper.getMainLooper()).post {
        try {
          ad.show(activity) { rewardItem ->
            val rewardData = mapOf(
              "type" to rewardItem.type,
              "amount" to rewardItem.amount
            )
            sendEvent("onRewarded", rewardData)
          }

          rewardedAd = null
          promise.resolve(true)
        } catch (e: Exception) {
          promise.reject("ERR_SHOW_FAILED", e.localizedMessage, e)
        }
      }
    }
  }
}