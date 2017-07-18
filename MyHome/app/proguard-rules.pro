# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/prokarma/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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


# Proguard rules for Picasso (found here: https://github.com/square/picasso)
-dontwarn com.squareup.okhttp.**

# Proguard rules for Retrofit (found here: http://square.github.io/retrofit/)
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-dontwarn okio.** ##Okio proguard rule (dependency of Retrofit)

# Proguard rules for GSON (found here: https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg)
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson uses generic type information stored in a class file when working with fields. Proguard
-keepattributes EnclosingMethod

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Our Proguard Rules for DignityHealth App
-keep class com.prokarma.myhome.utils.Constants { public static *; }

-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.**
#-keep class com.prokarma.myhome.features.login.LoginRequest { *; }
#-keep class com.prokarma.myhome.features.login.LoginResponse { *; }

## Google Play Services specific rules ##
## https://developer.android.com/google/play-services/setup.html#Proguard ##

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

## Square Picasso specific rules ##
## https://square.github.io/picasso/ ##
-dontwarn com.squareup.okhttp.**

## appcompat specific rules ##
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
## design specific rules ##
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }



# Retrofit 2.X
## https://square.github.io/retrofit/ ##

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses

-keep class !com.prokarma.myhome.** { *; }
-dontwarn !com.prokarma.myhome.**,**


-keep class com.prokarma.myhome.features.login.LoginResponse{*;}
-keep class com.prokarma.myhome.features.login.LoginRequest{*;}
-keep class com.prokarma.myhome.features.login.LoginRequest$Options{*;}
-keep class com.prokarma.myhome.features.enrollment.EnrollmentRequest{*;}
-keep class com.prokarma.myhome.features.enrollment.EnrollmentRequest$RecoveryQuestion{*;}
-keep class com.prokarma.myhome.features.profile.Profile{*;}
-keep class com.prokarma.myhome.features.profile.ProfileResponse{*;}
-keep class com.prokarma.myhome.features.login.forgot.password.ForgotPasswordRequest{*;}
-keep class com.prokarma.myhome.features.login.forgot.password.ForgotPasswordResponse{*;}
-keep class com.prokarma.myhome.features.appointments.Appointment{*;}
-keep class com.prokarma.myhome.features.appointments.AppointmentResponse{*;}
-keep class com.prokarma.myhome.features.appointments.AppointmentResponse$Result{*;}
-keep class com.prokarma.myhome.features.appointments.AppointmentResponse$Errors{*;}
-keep class com.prokarma.myhome.features.appointments.AppointmentResponse$Warnings{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentRequest{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse$Attributes{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse$Data{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse$Jsonapi{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse$Value{*;}
-keep class com.prokarma.myhome.features.fad.details.ProviderDetailsResponse{*;}
-keep class com.prokarma.myhome.features.fad.suggestions.SearchSuggestionResponse{*;}
-keep class com.prokarma.myhome.features.fad.ProvidersResponse{*;}
-keep class com.prokarma.myhome.features.tos.Tos{*;}
-keep class com.prokarma.myhome.features.tos.Result{*;}
-keep class com.prokarma.myhome.features.profile.signout.CreateSessionResponse{*;}
-keep class com.prokarma.myhome.features.fad.LocationResponse{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.validation.RegValidationResponse{*;}
-keep class com.prokarma.myhome.features.enrollment.ValidateEmailResponse{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.validation.Jsonapi{*;}
-keep class com.prokarma.myhome.features.fad.recent.RecentlyViewedSQLiteHelper{*;}
-keep class com.google.android.gms.maps.model.zza{*;}
-keep class com.squareup.okhttp.**{*;}
-keep class com.squareup.picasso.**{*;}
-dontwarn com.squareup.picasso.**
-dontwarn com.squareup.okhttp.**
-keep class com.prokarma.myhome.features.fad.CommonModel{*;}
-keep class com.prokarma.myhome.features.fad.Office{*;}
-keep class com.prokarma.myhome.features.profile.Address{*;}
-keep class com.prokarma.myhome.features.fad.details.bookingBookingSelectPersonInterface{*;}
-keep class com.prokarma.myhome.features.fad.details.Image{*;}
-keep class com.google.android.gms.maps.model.Marker{*;}
-keep class com.prokarma.myhome.features.fad.Facility{*;}
-keep class com.prokarma.myhome.features.fad.AppointmentType{*;}
-keep class com.prokarma.myhome.features.fad.Appointment{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.validation.RegIncluded{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.validation.RegValidationResponse{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.validation.RegValue{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.validation.RegAttributes{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.Jsonapi{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.Attributes{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.Relationships{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.Data{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.AppointmentDetails{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.Schedule{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.Error{*;}
-keep class com.prokarma.myhome.features.fad.details.booking.req.scheduling.Error$Source{*;}
-keep class com.prokarma.myhome.features.update.UpdateResponse{*;}
-keep class com.prokarma.myhome.features.update.UpdateResponse$Ciam{*;}
-keep class com.prokarma.myhome.features.update.UpdateResponse$Clients{*;}
-keep class com.prokarma.myhome.features.update.UpdateResponse$MyhomeMobileAndroid{*;}
-keep class com.prokarma.myhome.features.update.UpdateResponse$MyhomeMobileIos{*;}
-keep class com.prokarma.myhome.features.update.UpdateResponse$Services{*;}



-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}



-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Cardview
-keep class android.support.v7.widget.RoundRectDrawable { *; }

## Square Otto specific rules ##
## https://square.github.io/otto/ ##
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}
