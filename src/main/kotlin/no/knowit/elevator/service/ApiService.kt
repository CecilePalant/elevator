package no.knowit.elevator.service

import io.ktor.http.HttpStatusCode
import kotlin.concurrent.thread
import kotlin.math.abs
import no.knowit.elevator.models.ApiException
import no.knowit.elevator.models.MotorController


enum class Direction {
    UP, DOWN, STILL
}


/**
 * Where the logic happens.
 */
class ApiService {

    val FLOOR_DISTANCE = 3F // distance between 2 floors = 3 meters
    val TRAVEL_SPEED = 5F // elevator travel speed = 5 meters / second
    val OPENING_TIME = 3 // total time for doors to open, wait and close = 3 seconds

    private val motor = MotorController(FLOOR_DISTANCE, TRAVEL_SPEED, OPENING_TIME)

    private var isEmergencyStop = false
    private var currentDirection = Direction.STILL
    private var currentFloor = 0

    private var callList = mutableListOf<Int>()

    init {
        thread(start = true) {
            updatePosition()
        }
    }

    /**
     * Called by GET /direction to return the elevator's direction.
     * Can be one of UP, DOWN, STILL
     */
    fun getTravelDirection(): Direction {
        return currentDirection
    }

    /**
     * Called by GET /floor to return the elevator's current floor.
     * Is the elevator is moving, returns the floor it just passed by.
     */
    fun getCurrentFloor(): Int {
        return currentFloor
    }

    /**
     * Called by POST /floor/{floor} to request that the elevator stops at that floor.
     * Does nothing if the floor has already been called and not yet stopped at
     */
    fun addFloorRequest(floor: Int) {
        // don't add floor call if already in the call list
        if (!callList.contains(floor)) {
            println("Floor $floor added to call list")
            callList.add(floor)
        }
    }

    /**
     * Called by GET /time/{floor} to return the time to reach that floor, in seconds
     */
    fun getEstimatedTimeToReachFloor(floor: Int): Float {
        // in the assumption scenario, this is unlikely
        if (!callList.contains(floor)) {
            throw ApiException("Floor not in call list", HttpStatusCode.BadRequest)
        }

        val nbFloors = if (currentFloor == floor) 0 else getNbFloors(floor)
        return (nbFloors * FLOOR_DISTANCE) / TRAVEL_SPEED
    }

    private fun getNbFloors(floor: Int): Int {
        var nbFloors = 0

        var eval = currentFloor
        for (value in callList) {

            if (floor in eval..value) {
                return nbFloors + abs(eval - floor)

            } else {
                nbFloors += abs(eval - floor)
                eval = value
            }
        }

        return nbFloors
    }

    /**
     * Called by POST /stop to return the time to reach that floor
     */
    fun emergencyStop() {
        isEmergencyStop = true

        // this is a deadlock, there should be a resume after emergency endpoint
        motor.stop()
        currentDirection = Direction.STILL
    }


    /**
     * Separate thread that starts when the application starts
     * and commands the motor controller to move the elevator accord to the list of floor calls.
     * Updates the current floor and the current direction
     */
    private fun updatePosition() {
        while (!isEmergencyStop) {

            if (callList.isNotEmpty()) {

                if (currentFloor == callList[0]) {
                    stopHere()

                } else {
                    updateDirection()
                    moveOneFloor()
                }
            }
        }
    }

    private fun stopHere() {
        motor.stop()
        currentDirection = Direction.STILL

        motor.openDoors()
        callList.removeIf { value -> value == currentFloor }
    }

    private fun updateDirection() {
        if (currentFloor < callList[0]) {
            currentDirection = Direction.UP

        } else if (currentFloor > callList[0]) {
            currentDirection = Direction.DOWN
        }
    }

    private fun moveOneFloor() {
        motor.move(currentDirection)
        if (currentDirection == Direction.UP) {
            currentFloor++
        } else {
            currentFloor--
        }
    }
}
