/*
 * File: Ship.java
 * Author: Marcus Jones
 * Date: 12 October 2019
 * Purpose: CMSC 335 Project 4
 */
package seaportprogram;

import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.tree.*;

public class Ship extends Thing implements Runnable {

    private double draft, length, weight, width;
    //arraylist of jobs for this ship
    private ArrayList<Job> shipJobs = new ArrayList<>();
    private Box jobBox;

    public Ship(Scanner sc, Box jobBox) {
        super(sc);
        this.jobBox = jobBox;//private jobBox= parameter jobBox
        if (sc.hasNextDouble()) {
            weight = sc.nextDouble();
            length = sc.nextDouble();
            width = sc.nextDouble();
            draft = sc.nextDouble();
        }
    } // end end Scanner constructor

    public DefaultMutableTreeNode getJobNodes() {
        //if this ship has no jobs return node: "NO JOBS"
        if (shipJobs.isEmpty()) {
            DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode("NO JOBS");
            return emptyNode;
        }
        //gets the jobs if the ship has any
        DefaultMutableTreeNode jobFolderNode = new DefaultMutableTreeNode("Jobs");
        for (int i = 0; i < shipJobs.size(); i++) {
            DefaultMutableTreeNode jobNode = new DefaultMutableTreeNode(shipJobs.get(i).getName() + " (" + shipJobs.get(i).getIndex() + ")");
            jobFolderNode.add(jobNode);
        }

        return jobFolderNode;
    }

    public void addJob(Job theJob) {
        if (shipJobs.contains(theJob) == false) {
            shipJobs.add(theJob);
        }
    }

    public ArrayList<Job> getJobList() {
        return shipJobs;
    }

    @Override
    public String toString() {
        return super.toString() + " Weight:" + weight + "  Length:" + length + "  Width:" + width + "  Draft:" + draft + " ";
    }

    public double getWeight() {
        return weight;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public double getDraft() {
        return draft;
    }

    @Override
    public void run() {
        JLabel shipStartLabel = new JLabel("SHIP: " + this.getName() + " " + this.getIndex() + " HAS STARTED. Parent:" + this.getParentIndex());
        jobBox.add(shipStartLabel);
        jobBox.repaint();

        //pool that executes all the jobs this ship has at once
        ExecutorService shipPool = Executors.newFixedThreadPool(shipJobs.size());
        for (int i = 0; i < shipJobs.size(); i++) {

            shipPool.submit(shipJobs.get(i));
        }
        //waits for the pool to finish to show that the ship has sailed
        shipPool.shutdown();
        try {
            shipPool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
        }
        JLabel shipEndLabel = new JLabel("SHIP: " + this.getName() + " " + this.getIndex() + " HAS SAILED");
        jobBox.add(shipEndLabel);
        jobBox.repaint();

    }
}
