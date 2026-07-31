import ExpoModulesCore

public class BetterMobileAdsModule: Module {
  public func definition() -> ModuleDefinition {
    Name("BetterMobileAds")

    Events("onChange")

    Function("hello") {
      return "Hello world! 👋"
    }
  }
}
