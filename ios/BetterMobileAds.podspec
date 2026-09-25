Pod::Spec.new do |s|
  s.name           = 'BetterMobileAds'
  s.version        = '1.0.0'
  s.summary        = 'BetterMobileAds is a smaller and more reliable Google AdMob Plugin for React Native '
  s.description    = 'react-native-google-mobile-ads was insisting not to work. So I did my own wrapper. idk 9/10'
  s.author         = ''
  s.homepage       = 'https://docs.expo.dev/modules/'
  s.platforms      = { :ios => '16.4' }
  s.source         = { git: '' }
  s.static_framework = true

  s.dependency 'ExpoModulesCore'
  s.dependency 'Google-Mobile-Ads-SDK', '~> 12.0'

  # Swift/Objective-C compatibility
  s.pod_target_xcconfig = {
    'DEFINES_MODULE' => 'YES',
  }

  s.source_files = "**/*.{h,m,mm,swift,hpp,cpp}"
end
