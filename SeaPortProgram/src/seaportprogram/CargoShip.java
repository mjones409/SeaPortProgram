/*
 * File: CargoShip.java
 * Author: Marcus Jones
 * Date: 12 October 2019
 * Purpose: CMSC 335 Project 4
 */
package seaportprogram;

import java.util.*;
import javax.swing.*;

public class CargoShip extends Ship {

    private double cargoValue, cargoVolume, cargoWeight;

    public CargoShip(Scanner sc, Box jobBox) {
        super(sc, jobBox);
        if (sc.hasNextDouble()) {
            cargoWeight = sc.nextDouble();
            cargoVolume = sc.nextDouble();
            cargoValue = sc.nextDouble();
        }
    }

    @Override
    public String toString() {
        return "Cargo ship: " + super.toString() + " Cargo Weight:" + cargoWeight + " Cargo Volume" + cargoVolume + " Cargo Value:" + cargoValue;
    }
}
