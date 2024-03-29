package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config

lateinit var client: RedissonClient

const val messagesTopic = "messages"
data class Message(val message: String, val magic: Int)

fun configureRedis() {
    val config = Config()
    // docker run -d --name my-redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest
    config.useSingleServer().address = "redis://localhost:6379"
    client = Redisson.create(config)

    client.getTopic(messagesTopic).addListener(Message::class.java) { channel, msg ->
        println("channel: $channel, msg: $msg")
    }
}

fun Routing.messageRouting() {
    post("/messages") {
        val message = call.request.queryParameters["message"] ?: return@post call.respondText(
            "Required request param \"message\" is missing", status = HttpStatusCode.BadRequest
        )
        client.getTopic(messagesTopic).publish(Message(message, 42))
        call.respond(HttpStatusCode.OK)
    }
}