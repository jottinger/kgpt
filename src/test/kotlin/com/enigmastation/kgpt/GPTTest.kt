package com.enigmastation.kgpt

import com.enigmastation.kgpt.model.GPTMessage
import com.enigmastation.kgpt.model.GPTMessageContainer
import com.enigmastation.kgpt.model.GPTResponse
import com.enigmastation.kgpt.model.NullGPTResponse
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
    fun issueSimpleQueryInvalidKey() {
        val gpt = GPT(apiKey)
        val data = gpt.query("What is the speed of an african laden swallow")
        assertTrue(data.first()?.contains("African Swallow") ?: false)
    }

    @Test(expectedExceptions = [HttpException::class])
    fun issueSimpleQueryIn() {
        val gpt = GPT(apiKey + "invalid")
        gpt.query("What is the speed of an african laden swallow")
    }

    @Test
    fun issueComplexQueryMixedTypes() {
        val gpt = GPT(apiKey)
        val data = gpt.query(
            "What is the speed of an african laden swallow",
            "Use only latin names for species".asSystem(),
            "Use imperial measurements".asSystem(),
        )
        println("LLM responded with '${data.first() ?: "nothing"}'")
        assertTrue(data.first()?.contains("Hirundo") ?: false)
    }

    @Test
    fun issueComplexQueryVararg() {
        val gpt = GPT(apiKey)
        val data = gpt.query(
            "What is the speed of an african laden swallow",
            "Use only latin names for species".asSystem(),
            "Use imperial measurements".asSystem(),
        )
        println(data.first())
        assertTrue(data.first()?.contains("Hirundo") ?: false)
    }

    @Test
    fun queryUsingActualContainer() {
        val gpt = GPT(apiKey)
        val data = gpt.query(GPTMessageContainer(listOf("how are YOU doing".asUser())))
        println(data.first())
    }

    @Test
    fun testWithNullKey() {
        val gpt=GPT()
        val data=gpt.query("whoop de doodle!")
        assertTrue(data is NullGPTResponse)
    }

    @Test
    fun includeConversation() {
        val gpt = GPT(apiKey)
        var data = gpt.query("What is the speed of a laden african barn swallow") as GPTResponse
        println(data.choices.map { it.message.content + "\n" })
        data = gpt.query(
            "What is the speed of an african laden swallow",
            data.choices.map { GPTMessage(it.message.content, it.message.role) },
            "Use only latin names for species".asSystem()
        ) as GPTResponse
        println(data.choices.map { it.message.content + "\n" })
        data = gpt.query(
            "What is the speed of an african laden swallow",
            data.choices.map { GPTMessage(it.message.content, it.message.role) },
            "Use only latin names for species".asSystem(),
            "Use only imperial measurements.".asSystem()
        ) as GPTResponse
        println(data.choices.map { it.message.content + "\n" })
    }

    @Test
    fun issueComplexQueryList() {
        val gpt = GPT(apiKey)
        val data = gpt.query(
            listOf(
                "What is the speed of an african laden swallow".asUser(),
                "Use only latin names for species".asSystem(),
                "Use imperial measurements".asSystem(),
            )
        )
        println(data.first())
        assertTrue(data.first()?.contains("Hirundo") ?: false)
    }

}