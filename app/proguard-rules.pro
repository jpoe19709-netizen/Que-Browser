# Add project specific ProGuard rules here.
-keep class com.viaadvancedbrowser.** { *; }
-keepclassmembers class com.viaadvancedbrowser.** { *; }

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }