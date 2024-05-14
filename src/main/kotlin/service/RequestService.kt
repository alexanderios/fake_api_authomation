package service

import io.qameta.allure.Step
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import models.PostRequest
import io.qameta.allure.Attachment

class RequestService {

    @Step("Sending POST request to create a post")
    fun createPost(postRequest: PostRequest): Response {
        logRequestDetails("POST", "https://jsonplaceholder.typicode.com/posts", postRequest)
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .post("https://jsonplaceholder.typicode.com/posts")
            .then()
            .log().status()
            .log().body()
            .extract()
            .response()
    }

    @Attachment(value = "Request details", type = "application/json")
    private fun logRequestDetails(method: String, url: String, requestBody: Any): String {
        return """
            {
                "method": "$method",
                "url": "$url",
                "body": "${requestBody}"
            }
        """.trimIndent()
    }
}
