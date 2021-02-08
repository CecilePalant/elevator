package no.knowit.elevator.models

import mu.KotlinLogging
import no.knowit.elevator.service.Direction


/**
 * Controls the elevator's motors.
 * Can stop, move up or down and open and close doors
 */
class MotorController(private val floorDistance: Float, private val travelSpeed: Float, private val openingTime: Int) {

    private val logger = KotlinLogging.logger {}

    fun stop() {
        logger.debug { "Elevator: stopped" }
    }

    fun move(direction: Direction) {
        logger.debug { "Elevator: moving 1 floor $direction" }
        Thread.sleep((floorDistance / travelSpeed * 1000L).toLong())
    }

    fun openDoors() {
        logger.debug { "Elevator: opening doors" }
        Thread.sleep(openingTime * 1000L)
        logger.debug { "Elevator: closing doors" }
    }
}
