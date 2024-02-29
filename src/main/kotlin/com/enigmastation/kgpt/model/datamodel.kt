package com.enigmastation.kgpt.model

import com.enigmastation.kgpt.asUser
import com.fasterxml.jackson.annotation.JsonProperty

data class GPTMessageContainer(
    @JsonProperty("messages")
    var messages: List<GPTMessage>,
    @JsonProperty("model")
    val model: String = "gpt-4",
    @JsonProperty("temperature")
    val temperature: Double = 0.7
) {
    operator fun plus(message: GPTMessage): GPTMessageContainer {
        messages = messages + message
        return this
    }

    operator fun plus(message: String): GPTMessageContainer {
        return plus(message.asUser())
    }

}

data class GPTMessage(
    @JsonProperty("content")
    val content: String,
    @JsonProperty("role")
    val role: String = "user"
)

open class BaseGPTResponse() {
    open fun first(): String? = null
}

data class GPTResponse(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("object")
    val completion: String,
    @JsonProperty("created")
    val created: Long,
    @JsonProperty("model")
    val model: String,
    @JsonProperty("choices")
    val choices: List<GPTChoice>,
    @JsonProperty("usage")
    val usage: GPTUsage
) : BaseGPTResponse() {
    override fun first() = choices.firstOrNull()?.message?.content
    fun toGPTMessageContainer(): GPTMessageContainer =
        GPTMessageContainer(
            choices.map { GPTMessage(it.message.content, it.message.role) },
            model = model
        )
}

class NullGPTResponse : BaseGPTResponse()

data class GPTUsage(
    @JsonProperty("prompt_tokens")
    val promptTokens: Int,
    @JsonProperty("completion_tokens")
    val completionTokens: Int,
    @JsonProperty("total_tokens")
    val totalTokens: Int
)

data class GPTChoice(
    @JsonProperty("message")
    val message: GPTChoiceMessage,
    @JsonProperty("index")
    val index: Int,
    @JsonProperty("finish_reason")
    val finishReason: String
)

data class GPTChoiceMessage(
    @JsonProperty("content")
    val content: String,
    @JsonProperty("role")
    val role: String
)
