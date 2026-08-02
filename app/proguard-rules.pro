# Keep kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class ir.farhangi.**$$serializer { *; }
-keepclassmembers class ir.farhangi.** {
    *** Companion;
}
-keepclasseswithmembers class ir.farhangi.** {
    kotlinx.serialization.KSerializer serializer(...);
}