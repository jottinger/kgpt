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

Alternatively, you can pass in a series of lines, which are 
converted to `GPTMessage` instances at user scope,
unless they're passed in with a different scope.
Here's an example, where the query is about an 
african laden swallow (because why not), but with
commands for the LLM at `system` scope being passed in
as well:

```kotlin
val gpt = GPT(apiKey)
val data = gpt.query(
    "What is the speed of an african laden swallow",
    "Use only latin names for species".asSystem(),
    "Use imperial measurements".asSystem(),
)
println("The LLM responded with ${response.first()?:"nothing"}")
```

If any type is passed in other than a `String` or a `GPTMessage`, 
an `IllegalArgumentException` is thrown.

This passes in a query (the question about the swallow) and instructs the system to respond only with latin names 
(which is not part of the query) *and* to use imperial 
measurements only.

## Exceptional conditions

1. If no API Key is supplied, the `GPT` instance will return a `NullGPTResponse` instance, which has no data and no state.
2. If the key is invalid, or any other HTTP Exception occurs, the `query()` call will throw a `com.enigmastation.kgpt.HttpException`.

## Testing

To test this codebase, you should get an API Key from whichever LLM you choose 
(OpenAI is the default), and plug it into a file called `.env`. There's an `.env.default`
in the repository, but it has a blank `apiKey` value and will not successfully 
test the API; it's only there as an example.

## Deployment

> This section is for the maintainer!

Deployment is a pain. This is what worked:

```bash
export MAVEN_OPTS="--add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.desktop/java.awt.font=ALL-UNNAMED" 
mvn -DskipTests=true clean dokka:javadocJar deploy
```