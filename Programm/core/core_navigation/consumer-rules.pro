# Consumer ProGuard rules for :core:core_navigation.

# kotlinx-serialization for type-safe routes
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
    public static <1>$$serializer INSTANCE;
}
