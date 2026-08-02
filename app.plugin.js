const { withAndroidManifest } = require("@expo/config-plugins");

module.exports = function withBetterMobileAds(config, props = {}) {
  return withAndroidManifest(config, async (config) => {
    const androidManifest = config.modResults;
    const appId =
      props.androidAppId || "ca-app-pub-3940256099942544~3347511713";

    const application = androidManifest.manifest.application[0];

    let metaDataList = application["meta-data"] || [];

    metaDataList = metaDataList.filter(
      (item) =>
        item["$"]["android:name"] !==
        "com.google.android.gms.ads.APPLICATION_ID",
    );
    metaDataList.push({
      $: {
        "android:name": "com.google.android.gms.ads.APPLICATION_ID",
        "android:value": appId,
      },
    });

    application["meta-data"] = metaDataList;
    return config;
  });
};
