# Consumer ProGuard rules for :feature:feature_auth.

# kotlinx-serialization DTOs
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
    public static <1>$$serializer INSTANCE;
}

# Retrofit interface (api)
-keep,allowobfuscation interface ru.studentai.feature.auth.data.remote.api.AuthApi
