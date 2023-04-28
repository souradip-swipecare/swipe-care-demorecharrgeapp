# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#Event bus rules
-keepattributes Annotation
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }


#--------------------------------------------------
# keep anything annotated with @Expose
-keepclassmembers public class * {
    @com.google.gson.annotations.Expose *;
}
# Also keep classes that @Expose everything
-keep @com.google.gson.annotations.Expose public class *

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature


-keep class de.greenrobot.** { *; }
-keep interface de.greenrobot.** { *; }

# If you do not use SQLCipher:
-dontwarn net.sqlcipher.database.**
# If you do not use RxJava:
-dontwarn rx.**

#New proguard rules---------------------

#Fingerprint
-keep class com.easypay.epmoney.epmoneylib.request_model.finger_model.uid.* {*;}

#Morpho
-keep class com.morpho.** { *; }
-keep interface com.morpho.** { *; }

#------------
-keep class * implements android.os.Parcelable { public static final android.os.Parcelable$Creator *; }
-keep class org.apache.http.** { *; }
-keep public class org.simpleframework.**{ *; }
-keep class org.simpleframework.xml.**{ *; }
-keep class org.simpleframework.xml.core.**{ *; }
-keep class org.simpleframework.xml.util.**{ *; }
-keep class com.shashank.sony.fancytoastlibrary.**{ *; }



# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

#----------------------------------------------
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#-keep class com.finopaytech.finosdk.models.sqlite.{;}
#-keep class sun.misc.Unsafe { *; }
# For using GSON @Expose annotation
# Gson specific classes
# Application classes that will be serialized/deserialized over Gson, keepclassmembers

#-keep class com.google.gson.stream.** { *; }

#-keep public class com.easypay.epmoney.epmoneylib.baseframework.model.* {;}

#GreenDao
#-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
#public static java.lang.String TABLENAME;
#}
#-keep class *$Properties {;}
#-dontwarn retrofit2.KotlinExtensions$*

# Retrofit
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**

## keep Enum in Response Objects
-keepclassmembers enum com.android.services.** { *; }
## keep classes and class members that implement java.io.Serializable from being removed or renamed
## Fixes "Class class com.twinpeek.android.model.User does not have an id field" execption
-keep class * implements java.io.Serializable {
    *;
}
## Rules for Retrofit2
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
#-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
#-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
## Rules for Gson
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
#-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Remove logs, don't forget to use 'proguard-android-optimize.txt' file in build.gradle
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}
# org.json
-keep class org.json.** { *; }
# coroutines
# Allow R8 to optimize away the FastServiceLoader.
# Together with ServiceLoader optimization in R8
# this results in direct instantiation when loading Dispatchers.Main
#-assumenosideeffects class kotlinx.coroutines.internal.MainDispatcherLoader

#-assumenosideeffects class kotlinx.coroutines.internal.FastServiceLoaderKt
#-keep class kotlinx.coroutines.android.AndroidDispatcherFactory {*;}

# Disable support for "Missing Main Dispatcher", since we always have Android main dispatcher
#-assumenosideeffects class kotlinx.coroutines.internal.MainDispatchersKt

# Statically turn off all debugging facilities and assertions
#-assumenosideeffects class kotlinx.coroutines.DebugKt

# coroutines
#-keepnames class kotlinx.** { *; }
#-keepclassmembernames class kotlinx.** {
#    volatile <fields>;
#}
#-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler
#-keep class kotlinx.coroutines.android.AndroidDispatcherFactory
#-keep class com.swipecare.payments.recharges.RechargeOffersActivity

#-keepclassmembers class com.codepath.models** { <fields>; }

# keep everything in this package from being removed or renamed
#-keep class com.swipecare.payments.recharges.** { *; }

# keep everything in this package from being renamed only
#-keepnames class com.swipecare.payments.recharges.** { *; }

# do not obfuscate model classes
-keep class com.swipecare.payments.da.** { *; }
-keep class com.swipecare.payments.model.** { *; }
-keep class com.swipecare.payments.profit.** {*; }
-keep class com.swipecare.payments.fund.** {*; }

# paysprint
-keep class com.paysprint.onboardinglib.** { *; }
-keep class com.paysprint.onboardinglib.*{ *; }


