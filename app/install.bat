cd app
adb uninstall info.sayederfanarefin.compass
adb install -r app-release.apk
adb shell am start -n "info.sayederfanarefin.compass/info.sayederfanarefin.compass.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
cd ..

//exit