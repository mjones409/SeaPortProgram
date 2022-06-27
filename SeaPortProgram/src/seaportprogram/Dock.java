/*
 * File: Dock.java
 * Author: Marcus Jones
 * Date: 12 October 2019
 * Purpose: CMSC 335 Project 4
 */
package seaportprogram;

import java.util.*;

public class Dock extends Thing {

    public Dock(Scanner sc) {
        super(sc);

    }

    @Override
    public String toString() {
        return "Dock: " + super.toString();
    }
}
