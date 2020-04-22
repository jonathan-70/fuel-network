package fuel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RealHttpLoaderTest {

    private lateinit var realHttpLoader: RealHttpLoader
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        realHttpLoader = RealHttpLoader(OkHttpClient())
        mockWebServer = MockWebServer()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test(expected = HttpException::class)
    fun `Unsuccessful 404 Error`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("Hello World"))

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        val string = withContext(Dispatchers.IO) {
            realHttpLoader.get(mockWebServer.url("get")).body!!.string()
        }

        val request = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("GET", request.method)
        assertEquals(string, "Hello World")
    }

    @Test
    fun getTestData() = runBlocking {
        mockWebServer.enqueue(MockResponse().setBody("Hello World"))

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        val string = withContext(Dispatchers.IO) {
            realHttpLoader.get(mockWebServer.url("get")).body!!.string()
        }

        val request = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("GET", request.method)
        assertEquals(string, "Hello World")
    }

    @Test
    fun postTestData() = runBlocking {
        mockWebServer.enqueue(MockResponse())

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        val requestBody = "Hi?".toRequestBody("text/html".toMediaType())

        withContext(Dispatchers.IO) {
            realHttpLoader.post(mockWebServer.url("post"), requestBody)
        }

        val request = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("POST", request.method)
        val utf8 = withContext(Dispatchers.IO) {
            request.body.readUtf8()
        }
        assertEquals("Hi?", utf8)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty response body for post`() = runBlocking {
        mockWebServer.enqueue(MockResponse())

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        val request = Request.Builder().data(mockWebServer.url("post")).build()

        withContext(Dispatchers.IO) {
            realHttpLoader.post(request)
        }

        val request1 = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("POST", request1.method)
    }

    @Test
    fun putTestData() = runBlocking {
        mockWebServer.enqueue(MockResponse())

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        val requestBody = "Hello There".toRequestBody("text/html".toMediaType())

        withContext(Dispatchers.IO) {
            realHttpLoader.put(mockWebServer.url("put"), requestBody)
        }

        val request = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("PUT", request.method)
        val utf8 = withContext(Dispatchers.IO) {
            request.body.readUtf8()
        }
        assertEquals("Hello There", utf8)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty response body for put`() = runBlocking {
        mockWebServer.enqueue(MockResponse())

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        val request = Request.Builder().data(mockWebServer.url("put")).build()

        withContext(Dispatchers.IO) {
            realHttpLoader.put(request)
        }

        val request1 = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("PUT", request1.method)
    }

    @Test
    fun patchTestData() = runBlocking {
        mockWebServer.enqueue(MockResponse())

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        val requestBody = "Hello There".toRequestBody("text/html".toMediaType())

        withContext(Dispatchers.IO) {
            realHttpLoader.patch(mockWebServer.url("patch"), requestBody)
        }

        val request = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("PATCH", request.method)
        val utf8 = withContext(Dispatchers.IO) {
            request.body.readUtf8()
        }
        assertEquals("Hello There", utf8)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty response body for patch`() = runBlocking {
        mockWebServer.enqueue(MockResponse())

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        val request = Request.Builder().data(mockWebServer.url("patch")).build()

        withContext(Dispatchers.IO) {
            realHttpLoader.patch(request)
        }

        val request1 = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("PATCH", request1.method)
    }

    @Test
    fun deleteTestData() = runBlocking {
        mockWebServer.enqueue(MockResponse().setBody("Hello World"))

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        val string = withContext(Dispatchers.IO) {
            realHttpLoader.delete(mockWebServer.url("delete"), null).body!!.string()
        }

        val request = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("DELETE", request.method)
        assertEquals(string, "Hello World")
    }

    @Test
    fun headTestData() = runBlocking {
        mockWebServer.enqueue(MockResponse())

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        withContext(Dispatchers.IO) {
            realHttpLoader.head(mockWebServer.url("head"))
        }

        val request = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("GET", request.method)
    }

    @Test
    fun connectTestData() = runBlocking {
        mockWebServer.enqueue(MockResponse())

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        withContext(Dispatchers.IO) {
            realHttpLoader.method(mockWebServer.url("connect"), "CONNECT", null)
        }

        val request = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("CONNECT", request.method)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty method for CONNECT`() = runBlocking {
        mockWebServer.enqueue(MockResponse())

        withContext(Dispatchers.IO) {
            mockWebServer.start()
        }

        val request = Request.Builder().data(mockWebServer.url("connect")).build()

        withContext(Dispatchers.IO) {
            realHttpLoader.method(request)
        }

        val request1 = withContext(Dispatchers.IO) {
            mockWebServer.takeRequest()
        }

        assertEquals("CONNECT", request1.method)
    }
}
