package test

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.qameta.allure.Allure
import io.qameta.allure.Description
import io.restassured.response.Response
import kotlinx.coroutines.*
import models.PostRequest
import models.PostResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import service.RequestService
import service.ValidationService
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import java.util.stream.Stream

class ApiTests {

    private val requestService = RequestService()
    private val validationService = ValidationService()
    private val gson = Gson()

    companion object {
        @JvmStatic
        fun postRequestProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(PostRequest(1, "", ""), PostResponse(1, 101, "", "")),
            Arguments.of(PostRequest(1, "SomeTitle", "SomeBody"),
                PostResponse(1, 101, "SomeTitle", "SomeBody")),
            Arguments.of(PostRequest(1, "SomeTitle", ""),
                PostResponse(1, 101, "SomeTitle", "")),
            Arguments.of(PostRequest(1, "", "SomeBody"),
                PostResponse(1, 101, "", "SomeBody"))
        )
    }

    @ParameterizedTest
    @MethodSource("postRequestProvider")
    @Description("Параметризованный POST тест")
    fun testPostRequests(postRequest: PostRequest, expectedPostResponse: PostResponse) {
        val response: Response = requestService.createPost(postRequest)
        val actualPostResponse = gson.fromJson(response.asString(), PostResponse::class.java)
        validationService.validatePostResponse(expectedPostResponse, actualPostResponse)
    }

    @Test
    @DisplayName("Поиск топ 10 частовстречаемых слов в 100 POST запросах")
    @Description("Поиск топ 10 частовстречаемых слов в 100 POST запросах")
    fun tenMostFrequentWords() = runBlocking {
        val pathToJsonFile = javaClass.classLoader.getResource("testdata/full_posts.json")?.path
        if (pathToJsonFile == null) {
            println("Файл не найден: testdata/full_posts.json")
            Allure.step("Файл не найден: testdata/full_posts.json")
            return@runBlocking
        }

        val postRequests: List<PostRequest>
        try {
            postRequests = readPostRequestsFromJson(pathToJsonFile)
        } catch (e: FileNotFoundException) {
            println("Файл не найден: $pathToJsonFile")
            Allure.step("Файл не найден: $pathToJsonFile")
            return@runBlocking
        } catch (e: Exception) {
            println("Ошибка при чтении файла: ${e.message}")
            Allure.step("Ошибка при чтении файла: ${e.message}")
            return@runBlocking
        }

        val actualPostResponses = Collections.synchronizedList(mutableListOf<PostResponse>())
        val jobs = postRequests.map { postRequest ->
            launch(Dispatchers.IO) {
                val response: Response = requestService.createPost(postRequest)
                val actualPostResponse = gson.fromJson(response.asString(), PostResponse::class.java)
                actualPostResponses.add(actualPostResponse)
            }
        }
        jobs.joinAll()

        val topWords = findTop10FrequentWords(actualPostResponses)
        printTopWords(topWords)
    }

    private fun findTop10FrequentWords(postResponses: List<PostResponse>): List<Pair<String, Int>> {
        val wordCount = mutableMapOf<String, Int>()

        for (response in postResponses) {
            val words = response.body.split("\\W+".toRegex()).filter { it.isNotBlank() }
            for (word in words) {
                val lowerCaseWord = word.lowercase(Locale.getDefault())
                wordCount[lowerCaseWord] = wordCount.getOrDefault(lowerCaseWord, 0) + 1
            }
        }

        return wordCount.entries
            .sortedByDescending { it.value }
            .take(10)
            .map { it.key to it.value }
    }

    private fun readPostRequestsFromJson(path: String): List<PostRequest> {
        val jsonFile = File(path)
        if (!jsonFile.exists()) {
            throw FileNotFoundException("Файл не найден: $path")
        }
        val jsonString = jsonFile.readText()
        val listPostRequestType = object : TypeToken<List<PostRequest>>() {}.type
        return gson.fromJson(jsonString, listPostRequestType)
    }

    private fun printTopWords(topWords: List<Pair<String, Int>>) {
        val resultBuilder = StringBuilder("Топ 10 частовстречаемых слов:\n")
        for ((index, wordCount) in topWords.withIndex()) {
            val result = "${index + 1}. ${wordCount.first} - ${wordCount.second}"
            println(result)
            resultBuilder.append(result).append("\n")
        }
        Allure.addAttachment("Top 10 Words", "text/plain", resultBuilder.toString())
    }
}
