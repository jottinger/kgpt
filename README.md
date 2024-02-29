# kgpt
A Kotlin interface to ChatGPT and other LLMs using the openai web specifications

Usage should be pretty simple. 
You create an object of type `GPT` with an API key 
retrieved from the service in question, and then use
the `query` method to retrieve an answer.

> It is absolutely necessary to use an API key. 
> This library does not support anonymous queries to LLMs.

In the simplest case, once you have a `GPT` instance, you can use a 
`query()` call with a simple `String` to request information.

```kotlin
val gpt = GPT(apiKey)
val response:GPTResponse = 
    gpt.query(
        "What is the speed of an african laden swallow?"
    )
println("The LLM responded with ${response.first()?:"nothing"}")
```

This will automatically format the provided `String` as a `user` message for the LLM.

Alternatively, you can pass in a series of `GPTMessage` instances, which have their own scope. Here's an example:

```kotlin
val gpt = GPT(apiKey)
val data = gpt.query(
    "What is the speed of an african laden swallow".asUser(),
    "Use only latin names for species".asSystem(),
    "Use imperial measurements".asSystem(),
)
println("The LLM responded with ${response.first()?:"nothing"}")
```

This passes in a query (the question about the swallow) and instructs the system to respond only with latin names 
(which is not part of the query) *and* to use imperial 
measurements only.

## Exceptional conditions

1. If no API Key is supplied, the `GPT` instance will return a `NullGPTResponse` instance, which has no data and no state.
2. If the key is invalid, or any other HTTP Exception occurs, the `query()` call will throw a `com.enigmastation.kgpt.HttpException`.