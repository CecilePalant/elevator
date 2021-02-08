package no.knowit.elevator

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import java.util.concurrent.TimeUnit
import no.knowit.elevator.models.ApiException
import no.knowit.elevator.service.ApiService

/**
 * Required to stop the application on exitProcess()
 */
fun main() {
    val config: Config = ConfigFactory.load()
    val port: Int = config.getInt("ktor.deployment.port")
    val host: String = config.getString("ktor.deployment.host")

    val server = embeddedServer(Netty, port = port, host = host) {
        module(host, port)
    }.start(false)
    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(1, 5, TimeUnit.SECONDS)
    })
    Thread.currentThread().join()
}

/**
 * The Elevator controller API
 */
@kotlin.jvm.JvmOverloads
@Suppress("unused") // Referenced in application.conf
fun Application.module(host: String, port: Int, testing: Boolean = false) {

    install(StatusPages) {
        exception<ApiException> { cause ->
            call.respond(cause.statusCode, cause.message)
            throw cause
        }
    }

    install(CallLogging)

    install(ContentNegotiation) {
        gson()
    }

    val service = ApiService()

    // map routes
    routing {

        get("/") {
            call.respond("Welcome to the next generation elevator system controller. Call GET http://$host:$port/floor to get the current floor.")
        }

        get("/direction") {
            call.respond(service.getTravelDirection())
        }

        get("/floor") {
            call.respond(service.getCurrentFloor())
        }

        post("/floor/{floor}") {
            call.respond(service.addFloorRequest(call.parameters["floor"]!!.toInt()))
        }

        get("/time/{floor}") {
            call.respond(service.getEstimatedTimeToReachFloor(call.parameters["floor"]!!.toInt()))
        }

        post("/stop") {
            call.respond(service.emergencyStop())
        }
    }
}
