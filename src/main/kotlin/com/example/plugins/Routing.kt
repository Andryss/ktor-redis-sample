package com.example.plugins

import com.example.messageRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        messageRouting()
    }
}
