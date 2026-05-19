# Consumer ProGuard rules for :core:core_ui.

# Compose state classes are accessed reflectively in some debug paths.
-keep class androidx.compose.runtime.** { *; }

# Hilt-generated classes — let Hilt's own rules handle them.
