-keepattributes InnerClasses
-keepattributes EnclosingMethod

-if @kotlinx.serialization.Serializable class
com.aallam.openai.api.**
{
    static **$* *;
}
-keepnames class <1>$$serializer {
    static <1>$$serializer INSTANCE;
}