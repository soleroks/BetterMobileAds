import { NativeModule, requireNativeModule } from 'expo';

import { BetterMobileAdsModuleEvents } from './BetterMobileAds.types';

declare class BetterMobileAdsModule extends NativeModule<BetterMobileAdsModuleEvents> {
  hello(): string;
}

export default requireNativeModule<BetterMobileAdsModule>('BetterMobileAds');
