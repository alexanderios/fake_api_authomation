package service

import models.PostResponse
import org.assertj.core.api.SoftAssertions
import io.qameta.allure.Step

class ValidationService {

    @Step("Validating post response")
    fun validatePostResponse(expected: PostResponse, actual: PostResponse) {
        val softly = SoftAssertions()
        softly.assertThat(actual.userId)
            .`as`("Checking that userId is equal to %s", expected.userId)
            .isEqualTo(expected.userId)
        softly.assertThat(actual.title)
            .`as`("Checking that title is equal to %s", expected.title)
            .isEqualTo(expected.title)
        softly.assertThat(actual.body)
            .`as`("Checking that body is equal to %s", expected.body)
            .isEqualTo(expected.body)
        softly.assertThat(actual.id)
            .`as`("Checking that id is equal to %s", expected.id)
            .isEqualTo(expected.id)
        softly.assertAll()
    }
}
