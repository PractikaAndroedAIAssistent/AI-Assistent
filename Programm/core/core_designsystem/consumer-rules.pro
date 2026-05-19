# Consumer ProGuard rules for :core:core_designsystem.
# These rules are automatically applied to any app that depends on this library.

# Compose uses runtime reflection on slot tables — keep companions to avoid
# accidental shrinkage in release builds.
-keep,allowobfuscation,allowshrinking class kotlin.Metadata
-keep class androidx.compose.runtime.** { *; }
