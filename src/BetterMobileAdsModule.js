import { requireNativeModule } from "expo-modules-core";

const nativeModule = requireNativeModule("BetterMobileAds");

export default {
  ...nativeModule,
  initialize(options = {}) {
    return nativeModule.initialize(options);
  },
};
