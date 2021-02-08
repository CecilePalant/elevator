package no.knowit.elevator.models

import io.ktor.http.HttpStatusCode

class ApiException(override val message: String, val statusCode: HttpStatusCode, override var cause: Throwable?) :
    Throwable() {
    constructor(message: String, statusCode: HttpStatusCode) : this(message, statusCode, null)
}
