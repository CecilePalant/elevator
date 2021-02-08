package no.knowit.elevator.models

import no.knowit.elevator.service.Direction


/**
 * Controls the elevator's motors.
 * Can stop, move up or down and open and close doors
 */
class MotorController {

    fun stop() {
        print("Elevator: stopped")
    }

    fun move(direction: Direction) {
        print("Elevator: moving 1 floor $direction")
    }

    fun openDoors() {
        print("Elevator: opening doors")
        print("Elevator: closing doors")
    }
}
