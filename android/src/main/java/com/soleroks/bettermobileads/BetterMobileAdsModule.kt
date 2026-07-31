package com.soleroks.bettermobileads

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class BetterMobileAdsModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("BetterMobileAds")

    Events("onChange")

    Function("hello") {
      "Hello world! 👋"
    }
  }
}
