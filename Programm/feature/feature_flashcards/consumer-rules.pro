# Consumer ProGuard rules for :feature:feature_flashcards.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
    public static <1>$$serializer INSTANCE;
}

-keep,allowobfuscation,allowshrinking class * {
    @retrofit2.http.* <methods>;
}
