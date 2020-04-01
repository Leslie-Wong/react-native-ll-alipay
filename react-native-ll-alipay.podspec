require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = "react-native-ll-alipay"
  s.version      = package['version']
  s.summary      = "React Native Alipay component for Android and iOS"

  s.authors      = { "LesLie.W" => "leslie.wmy@gmail.com" }
  s.homepage     = "https://github.com/Leslie-Wong/react-native-ll-alipay"
  s.license      = package['license']
  s.platform     = :ios, "9.0"

  s.source       = { :git => "https://github.com/Leslie-Wong/react-native-ll-alipay" }
  s.source_files  = "ios/RNAlipay/RNAlipay.{h,m}"

  s.dependency 'React'
end
