import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The quality of the image that will be generated
 */
@Serializable
@JvmInline
public value class Quality(public val value: String) {
    public companion object {
        public val HD: Quality = Quality("hd")
    }
}
