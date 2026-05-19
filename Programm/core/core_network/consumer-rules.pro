# Consumer ProGuard rules for :core:core_network.

# Retrofit / OkHttp reflection on annotated interfaces.
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, AnnotationDefault

# Keep all annotations on Retrofit service methods.
-keepclasseswithmembers,allowobfuscation,allowshrinking class * {
    @retrofit2.http.* <methods>;
}

# Keep @NoAuth marker for runtime detection by AuthInterceptor.
-keep,allowobfuscation @interface ru.studentai.core.network.auth.NoAuth
-keep,allowobfuscation,allowshrinking class * {
    @ru.studentai.core.network.auth.NoAuth <methods>;
}

# kotlinx-serialization
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
    public static <1>$$serializer INSTANCE;
}
-keepclassmembers class **$$serializer {
    static **$$serializer INSTANCE;
}
