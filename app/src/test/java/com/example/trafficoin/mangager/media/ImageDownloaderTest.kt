package com.example.trafficoin.mangager.media

import android.content.Context
import com.example.trafficoin.MainDispatcherRule
import com.example.trafficoin.manager.media.ImageDownloader
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.*
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.*
import java.io.File
import java.io.IOException
import java.security.MessageDigest

/**
 * @author Jack
 */
class ImageDownloaderTest {
    // 處理 Coroutines (必備)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testURL = "https://example.com/test.png"
    private val mDigest: MessageDigest by lazy { MessageDigest.getInstance("SHA-256") }

    private val mockContext = mockk<Context>()
    private val mockClient = mockk<OkHttpClient>()
    private lateinit var downloader: ImageDownloader

    // 建立一個真實的暫存目錄
    private val tempDir = File("build/tmp/test_pics").apply { mkdirs() }
    private lateinit var picsDir: File

    @Before
    fun setup() {
        File(tempDir, "pics").apply { mkdirs() }
        every { mockContext.cacheDir } returns tempDir
        picsDir = File(tempDir, "pics").apply { mkdirs() }

        downloader = spyk(ImageDownloader(mockContext, mockClient))
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
        unmockkAll()
    }

    @Test
    fun `download - when file already exists`() = runTest {
        // 在實體暫存路徑建立檔案
        val realFile = File(picsDir, testURL.toSha256())
        realFile.writeText("fake content")

        val result = downloader.download(testURL)

        assert(result != null)
        assert(result?.exists() == true)
        assert(result?.absolutePath == realFile.absolutePath)

        verify(exactly = 0) { mockClient.newCall(any()) }

        realFile.delete()
    }

    @Test
    fun `download - when file not exists and return file`() = runTest {
        val mockResponse = mockkOkHttpClient()
        val content = "fake image data"
        val realResponseBody = content.toResponseBody("image/png".toMediaTypeOrNull())
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body } returns realResponseBody

        val result = downloader.download(testURL)
        verify { mockClient.newCall(match { it.url.toString() == testURL }) }
        assert(result != null)
    }

    @Test
    fun `download - when file not exists and return null after retry`() = runTest {
        val mockResponse = mockkOkHttpClient()
        every { mockResponse.isSuccessful } returns false

        val result = downloader.download(testURL)
        verify(exactly = 3) { mockClient.newCall(any()) }
        assert(result == null)
    }

    @Test
    fun `download - when network fails`() = runTest {
        every { mockClient.newCall(any()) } throws IOException("Network down")

        val result = downloader.download(testURL)
        assert(result == null)
    }



    private fun mockkOkHttpClient(slot: CapturingSlot<Request>? = null): Response {
        val mockCall = mockk<Call>(relaxed = true)
        val mockResponse = mockk<Response>(relaxed = true)
        every { mockClient.newCall(slot?.let { capture(it) } ?: any<Request>()) } returns mockCall
        every { mockCall.execute() } returns mockResponse
        return mockResponse
    }

    private fun String.toSha256(): String {
        return mDigest.digest(encodeToByteArray())
            .joinToString(separator = "", transform = "%02x"::format)
    }
}