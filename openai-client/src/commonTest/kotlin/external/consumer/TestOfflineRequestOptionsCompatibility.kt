package external.consumer

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.http.Timeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class TestOfflineRequestOptionsCompatibility {
    @Test
    fun inferredConstructorReferenceRetainsThreeArguments() {
        val constructor = ::RequestOptions
        val headers = mapOf("X-Test" to "original")
        val parameters = mapOf("page" to "1")
        val timeout = Timeout(socket = 10.seconds)

        val options = constructor(headers, parameters, timeout)

        assertEquals(headers, options.headers)
        assertEquals(parameters, options.urlParameters)
        assertEquals(timeout, options.timeout)
    }

    @Test
    fun inferredBoundCopyReferenceRetainsThreeArguments() {
        val original = RequestOptions()
        val copy = original::copy
        val headers = mapOf("X-Test" to "copied")
        val parameters = mapOf("page" to "2")
        val timeout = Timeout(socket = 20.seconds)

        assertEquals(RequestOptions(headers, parameters, timeout), copy(headers, parameters, timeout))
        assertEquals(RequestOptions(), original)
    }
}
