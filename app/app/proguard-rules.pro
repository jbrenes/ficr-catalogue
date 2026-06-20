-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class it.ficr.pagaiacronos.data.remote.dto.** { *; }
