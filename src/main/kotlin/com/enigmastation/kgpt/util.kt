package com.enigmastation.kgpt

import com.enigmastation.kgpt.model.GPTMessage

fun String.asSystem()= GPTMessage(this,"system")
fun String.asUser()= GPTMessage(this,"user")
fun String.asAssistant()= GPTMessage(this,"assistant")
