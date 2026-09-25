const unsupported = () =>
  Promise.reject(new Error("BetterMobileAds is not supported on web."));

export default {
  initialize: unsupported,
  getVersion: () => "unsupported",
  loadInterstitial: unsupported,
  showInterstitial: unsupported,
  loadRewarded: unsupported,
  showRewarded: unsupported,
};
