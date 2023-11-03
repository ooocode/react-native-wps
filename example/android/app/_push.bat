adb root
adb remount
adb push C:\codes\react-native-wps\react-native-wps\example\android\app\build\outputs\apk\release\app-release.apk /system/priv-app
adb remount
adb shell chmod 777 /system/priv-app/app-release.apk
adb reboot
