/*
 * File: PassengerShip.java
 * Author: Marcus Jones
 * Date: 12 October 2019
 * Purpose: CMSC 335 Project 4
 */
package seaportprogram;

import java.util.*;
import javax.swing.*;

public class PassengerShip extends Ship {

    private int numOccupiedRooms, numPassengers, numRooms;

    public PassengerShip(Scanner sc, Box jobBox) {
        super(sc, jobBox);
        numPassengers = sc.nextInt();
        numRooms = sc.nextInt();
        numOccupiedRooms = sc.nextInt();
    } // end end Scanner constructor

    @Override
    public String toString() {
        return "Passenger ship: " + super.toString() + " Number of Passengers:" + numPassengers
                + " Number of Rooms:" + numRooms + " Number of Occupied Rooms:" + numOccupiedRooms;
    }
}
