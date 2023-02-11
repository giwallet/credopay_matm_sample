#-keep class in.credopay.**{
#<fields>;
#public <methods>;
#}
#-keepclassmembers class in.credopay.** { <fields>; }
-keepclassmembers class in.credopay.payment.sdk.ApiRequest {
<fields>; }
-keepclassmembers class in.credopay.payment.sdk.ApiResponse {
 <fields>;
}
-keepclassmembers class in.credopay.payment.sdk.ApiRequest$IsoData {
<fields>; }
-keepclassmembers class in.credopay.payment.sdk.ApiResponse$IsoData {
<fields>; }
-keepclassmembers class in.credopay.payment.sdk.ApiResponse$IsoData {
<fields>; }
-keepclassmembers class in.credopay.payment.sdk.ApiErrorResponse {
<fields>; }
-keepclassmembers class in.credopay.payment.sdk.TransactionModel {
<fields>; }
-keepclassmembers class in.credopay.payment.sdk.TransactionResponse {
<fields>;
}
-keepclassmembers class in.credopay.payment.sdk.UpiStatusResponse {
<fields>;
}
-keepclassmembers class
in.credopay.payment.sdk.ApiResponse$TransactionSets {
<fields>;
}
-keepclassmembers class
in.credopay.payment.sdk.TransactionAggregateResponse {
<fields>;
}
-assumenosideeffects class android.util.Log {
public static boolean isLoggable(java.lang.String, int);
public static int d(...);
public static int w(...);
public static int v(...);
public static int i(...);
public static int e(...);
}
-assumenosideeffects class timber.log.Timber* {
public static * d(...);
public static * w(...);
public static * v(...);
public static * i(...);
public static * e(...);
}