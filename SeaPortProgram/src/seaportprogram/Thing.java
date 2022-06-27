/*
 * File: Thing.java
 * Author: Marcus Jones
 * Date: 12 October 2019
 * Purpose: CMSC 335 Project 4
 */
package seaportprogram;

import java.util.*;

public class Thing implements Comparable<Thing> {

    private int index;
    private String name;
    private int parent;
//SeaPort Constructor

    public Thing() {
    }
//SeaPort Constructor

    public Thing(Scanner sc) {
        if (sc.hasNext()) {
            name = sc.next();
        }
        if (sc.hasNextInt()) {
            index = sc.nextInt();
            parent = sc.nextInt();
        }
    }
//get methods

    public int getIndex() {
        return index;
    }

    public int getParentIndex() {
        return parent;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " Index:" + index + " Parent:" + parent + " ";
    }

    //compares by name
    @Override
    public int compareTo(Thing o) {
        return this.name.compareTo(o.name);
    }

}
