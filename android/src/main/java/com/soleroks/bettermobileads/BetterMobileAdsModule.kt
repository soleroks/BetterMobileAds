package com.soleroks.bettermobileads

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class BetterMobileAdsModule : Module() {

  private var interstitialAd: InterstitialAd? = null

  override fun definition() = ModuleDefinition {

    Name("BetterMobileAds")

    Function("initialize") {
      val activity = appContext.activityProvider?.currentActivity ?: return@Function false

      activity.runOnUiThread {
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
        promise.reject("ERR_ACTIVITY_NULL", "Activity null. Uygulama ön planda mı?", null)
        return@AsyncFunction
      }

      activity.runOnUiThread {
        try {
          val adRequest = AdRequest.Builder().build()

          // KRİTİK DEĞİŞİKLİK: Context yerine 'activity' geçiyoruz.
          // AdMob dahili WebView/JS Engine başlatırken Activity Context'ine ihtiyaç duyar.
          InterstitialAd.load(
            activity, 
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {

              override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                promise.resolve(true)
              }

              override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
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
        promise.reject("ERR_AD_NOT_LOADED", "Interstitial ad henüz yüklenmedi", null)
        return@AsyncFunction
      }

      activity.runOnUiThread {
        try {
          ad.show(activity)
          interstitialAd = null
          promise.resolve(true)
        } catch (e: Exception) {
          promise.reject("ERR_SHOW_FAILED", e.localizedMessage, e)
        }
      }
    }
  }
}