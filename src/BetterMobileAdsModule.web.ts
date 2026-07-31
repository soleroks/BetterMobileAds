import { registerWebModule, NativeModule } from 'expo';

import { BetterMobileAdsModuleEvents } from './BetterMobileAds.types';

// BetterMobileAdsModule is not available on the web platform.
class BetterMobileAdsModule extends NativeModule<BetterMobileAdsModuleEvents> {}

export default registerWebModule(BetterMobileAdsModule, 'BetterMobileAdsModule');
