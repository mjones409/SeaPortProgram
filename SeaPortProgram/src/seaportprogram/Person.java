/*
 * File: Person.java
 * Author: Marcus Jones
 * Date: 12 October 2019
 * Purpose: CMSC 335 Project 4
 */
package seaportprogram;

import java.util.*;

public class Person extends Thing {

    private String skill;
    /*isPerson will be false if I 
    inserted a blank person using
    the Person(boolean) constructor*/
    private boolean isPerson = true;

    public Person(Scanner sc) {
        super(sc);
        skill = sc.next();
    }

    public Person(boolean isPerson) {
        this.isPerson = false;
        this.skill = "NOTHING";

    }

    public String getSkill() {
        return skill;
    }

    @Override
    public String toString() {
        return "People: " + super.toString() + " Skill:" + skill;
    }

    //toString method for display in the Resource Pool
    public String toString(boolean isGUI) {
        return this.getName() + ":" + this.getSkill() + ",";
    }

    public boolean isPerson() {
        return isPerson;
    }
}
