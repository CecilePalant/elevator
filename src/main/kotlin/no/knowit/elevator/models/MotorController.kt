package no.knowit.elevator.models

import no.knowit.elevator.service.Direction


/**
 * Controls the elevator's motors.
 * Can stop, move up or down and open and close doors
 */
class MotorController(private val floorDistance: Float, private val travelSpeed: Float, private val openingTime: Int) {


    fun stop() {
        println("Elevator: stopped")
    }

    fun move(direction: Direction) {
        println("Elevator: moving 1 floor $direction")
        Thread.sleep((floorDistance / travelSpeed * 1000L).toLong())
    }

    fun openDoors() {
        println("Elevator: opening doors")
        Thread.sleep(openingTime * 1000L)
        println("Elevator: closing doors")
    }
}
