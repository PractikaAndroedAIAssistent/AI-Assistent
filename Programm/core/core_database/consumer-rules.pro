# Consumer ProGuard rules for :core:core_database.
# Room generates code at build time; keep schema annotations and type converters.

-keep class androidx.room.RoomDatabase { *; }
-keepclasseswithmembers,allowobfuscation class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# kotlinx-serialization (for StringListConverter)
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
