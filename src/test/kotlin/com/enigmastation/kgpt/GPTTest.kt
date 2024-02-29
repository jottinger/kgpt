package com.enigmastation.kgpt

import io.github.cdimascio.dotenv.dotenv
import org.testng.Assert.assertTrue
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.net.URI

class GPTTest {
    val apiKey = dotenv().get("apiKey")

    @DataProvider
    fun constructorInputs() =
        arrayOf(
            arrayOf(null, URI("https://api.openai.com/v1/chat/completions")),
            arrayOf("foobar", URI("https://api.openai.com/v1/chat/completions")),
        )

    @Test(dataProvider = "constructorInputs")
    fun acceptDifferentKeys(key: String?, url: URI) {
        GPT(key, url)
    }

    @Test
    fun buildSimpleGPTInstance() {
        val gpt = GPT(apiKey)
    }

    @Test
    fun issueSimpleQuery() {
        val gpt = GPT(apiKey)
        val data = gpt.query("What is the speed of an african laden swallow")
        assertTrue(data.first()?.contains("African Swallow") ?: false)
    }
}