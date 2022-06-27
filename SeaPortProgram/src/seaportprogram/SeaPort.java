/*
 * File: SeaPort.java
 * Author: Marcus Jones
 * Date: 12 October 2019
 * Purpose: CMSC 335 Project 4
 */
package seaportprogram;

import java.util.*;
import java.util.concurrent.*;
import javax.swing.tree.*;
import javax.swing.*;

public class SeaPort extends Thing implements Runnable {

    private ArrayList<Dock> docks = new ArrayList<>();// arraylist of docks
    private ArrayList<Ship> ships = new ArrayList<>(); // a arraylist of all the ships at this port
    private volatile ArrayList<Person> persons = new ArrayList<>(); // people with skills at this port
    private Box jobBox;
    private ArrayList<String> skills = new ArrayList<>(); // skills at this port
    private Box peopleBox;
    private volatile JLabel peopleLabel;
    private String defaultLabel;

    public SeaPort(Scanner sc, Box jobBox, Box peopleBox) {
        super(sc);
        this.jobBox = jobBox;
        this.peopleBox = peopleBox;
        defaultLabel = "<html>Port: " + this.getName() + "(" + this.getIndex() + ")";
        peopleLabel = new JLabel(defaultLabel);
        peopleBox.add(peopleLabel);
        peopleBox.repaint();
    }

    @SuppressWarnings("unchecked")
    public void assignDock(Dock theDock, HashMap hmThings) {
        docks.add(theDock);
        hmThings.put(theDock.getIndex(), theDock);
    }

    @SuppressWarnings("unchecked")
    public void assignPerson(Person thePerson, HashMap hmThings) {
        persons.add(thePerson);
        hmThings.put(thePerson.getIndex(), thePerson);
    }

    @SuppressWarnings("unchecked")
    public void assignShip(Ship theShip, HashMap hmThings) {
        ships.add(theShip);
        hmThings.put(theShip.getIndex(), theShip);
    }

    @SuppressWarnings("unchecked")
    public boolean assignPShip(PassengerShip PShip, HashMap hmThings) {
        boolean assigned = false;
        for (int i = 0; i < docks.size(); i++) {
            if (docks.get(i).getIndex() == PShip.getParentIndex()) {
                ships.add(PShip);
                assigned = true;
                hmThings.put(PShip.getIndex(), PShip);

            }
        }
        return assigned;
    }

    public void sortShips(int sortSelection) {
        if (sortSelection == 0) {
            //sort ships by name
            Collections.sort(ships);
        }
        if (sortSelection == 1) {
            //comparator to sort ships by weight
            Collections.sort(ships, new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    if (o1.getWeight() > o2.getWeight()) {
                        return 1;
                    }
                    if (o1.getWeight() < o2.getWeight()) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        if (sortSelection == 2) {
            //comparator to sort ships by length
            Collections.sort(ships, new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    if (o1.getLength() > o2.getLength()) {
                        return 1;
                    }
                    if (o1.getLength() < o2.getLength()) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        if (sortSelection == 3) {

            //comparator to sort ships by width
            Collections.sort(ships, new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    if (o1.getWidth() > o2.getWidth()) {
                        return 1;
                    }
                    if (o1.getWidth() < o2.getWidth()) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        if (sortSelection == 4) {

            //comparator to sort ships by draft
            Collections.sort(ships, new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    if (o1.getDraft() > o2.getDraft()) {
                        return 1;
                    }
                    if (o1.getDraft() < o2.getDraft()) {
                        return -1;
                    }
                    return 0;
                }
            });
            //sorting complete
        }
    }

    //getting nodes
//creates the tree to be displayed to the user
    public DefaultMutableTreeNode getNodes(DefaultMutableTreeNode portNode) {
        DefaultMutableTreeNode personFolderNode = new DefaultMutableTreeNode("People");
        DefaultMutableTreeNode dockFolderNode = new DefaultMutableTreeNode("Docks");
        DefaultMutableTreeNode cShipFolderNode = new DefaultMutableTreeNode("Cargo Ships");
        DefaultMutableTreeNode portPShipFolderNode = new DefaultMutableTreeNode("Passenger Ships");

//adding people to tree
        for (int i = 0; i < persons.size(); i++) {
            DefaultMutableTreeNode personNode = new DefaultMutableTreeNode(persons.get(i).getName() + " (" + persons.get(i).getIndex() + ") " + persons.get(i).getSkill());
            personFolderNode.add(personNode);
            portNode.add(personFolderNode);
        }
//adding cargo ships to tree & their jobs
        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i).getClass() == CargoShip.class) {
                DefaultMutableTreeNode cShipNode = new DefaultMutableTreeNode(ships.get(i).getName() + " (" + ships.get(i).getIndex() + ")");
                cShipFolderNode.add(cShipNode);
                cShipNode.add(ships.get(i).getJobNodes());
                portNode.add(cShipFolderNode);
            }
        }
//adding passenger ships attached to seaports to tree & their jobs
        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i).getClass() == PassengerShip.class && ships.get(i).getParentIndex() == this.getIndex()) {
                DefaultMutableTreeNode pShipNode = new DefaultMutableTreeNode(ships.get(i).getName() + " (" + ships.get(i).getIndex() + ")");
                portPShipFolderNode.add(pShipNode);
                pShipNode.add(ships.get(i).getJobNodes());
                portNode.add(portPShipFolderNode);
            }
        }

//adding passenger ships attached to docks to tree & their jobs
        for (int i = 0; i < docks.size(); i++) {
            DefaultMutableTreeNode pShipFolderNode = new DefaultMutableTreeNode("Passenger Ships");
            DefaultMutableTreeNode dockNode = new DefaultMutableTreeNode(docks.get(i).getName() + " (" + docks.get(i).getIndex() + ")");
            portNode.add(dockFolderNode);
            dockFolderNode.add(dockNode);
            for (int j = 0; j < ships.size(); j++) {
                if (docks.get(i).getIndex() == ships.get(j).getParentIndex()) {
                    DefaultMutableTreeNode pShipNode = new DefaultMutableTreeNode(ships.get(j).getName() + " (" + ships.get(j).getIndex() + ")");
                    pShipFolderNode.add(pShipNode);
                    pShipNode.add(ships.get(i).getJobNodes());
                }
            }
            dockNode.add(pShipFolderNode);
        }

        return portNode;
    }

    @Override
    public String toString() {
        //sorting all the arraylists by name
        Collections.sort(persons);
        Collections.sort(docks);

        String shipString = "";
        for (int i = 0; i < ships.size(); i++) {
            shipString = shipString + ships.get(i).toString() + "\n";
        }
        String personString = "";
        for (int i = 0; i < persons.size(); i++) {
            personString = personString + persons.get(i).toString() + "\n";
        }
        String dockString = "";
        for (int i = 0; i < docks.size(); i++) {
            dockString = dockString + docks.get(i).toString() + "\n";
            for (int j = 0; j < ships.size(); j++) {
                if (docks.get(i).getIndex() == ships.get(j).getParentIndex()) {
                    dockString = dockString + ships.get(j).toString() + "\n\n";
                }
            }
        }
        String jobString = "";
        for (int i = 0; i < ships.size(); i++) {
            for (int j = 0; j < ships.get(i).getJobList().size(); j++) {
                jobString = jobString + ships.get(i).getJobList().get(j).toString() + "\n";
            }
        }

        return "SeaPort: " + super.toString() + "\n\n" + dockString
                + "\n" + shipString + "\n" + personString + "\n" + jobString + " ";

    }

    //searches by name for people, ships, and docks
    public String searchMoreNames(int choice, String userString) {
        String out = "";
        String peopleOut = "";
        String shipOut = "";
        String dockOut = "";
        String jobOut = "";
        //searches docks
        if (choice == 1) {
            for (int i = 0; i < docks.size(); i++) {
                if (docks.get(i).getName().toLowerCase().contains(userString.toLowerCase())) {
                    dockOut = dockOut + docks.get(i).toString() + "\n";
                }
            }
            out = dockOut;
        }
        //searches ships
        if (choice == 2) {
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).getName().toLowerCase().contains(userString.toLowerCase())) {
                    shipOut = shipOut + ships.get(i).toString() + "\n";
                }
            }
            out = shipOut;
        }
        //searches for people
        if (choice == 3) {
            for (int i = 0; i < persons.size(); i++) {
                if (persons.get(i).getName().toLowerCase().contains(userString.toLowerCase())) {
                    peopleOut = peopleOut + persons.get(i).toString() + "\n";
                }
            }
            out = peopleOut;
        }
        //searches jobs
        if (choice == 4) {
            for (int i = 0; i < ships.size(); i++) {
                for (int j = 0; j < ships.get(i).getJobList().size(); j++) {
                    if (ships.get(i).getJobList().get(j).getName().toLowerCase().contains(userString.toLowerCase())) {
                        jobOut = jobOut + ships.get(i).getJobList().get(j).toString() + "\n";
                    }
                }
            }
            out = jobOut;
        }
        return out;
    }
//searches for people by skill

    public String skillSearch(String userSearch) {
        String peopleOut = "";
        if (persons.isEmpty() == false) {
            for (int i = 0; i < persons.size(); i++) {
                if (persons.get(i).getSkill().toLowerCase().contains(userSearch.toLowerCase())) {
                    peopleOut = peopleOut + persons.get(i).toString() + "\n";
                }
            }
        }
        return peopleOut;
    }
//searches for docks, ships, and people by index

    public String indexSearch(int choice, String indexToSearch) {

        String out = "";
        String peopleOut = "";
        String shipOut = "";
        String dockOut = "";
        String jobOut = "";
        //searches docks
        if (choice == 1) {
            for (int i = 0; i < docks.size(); i++) {
                if (Integer.toString(docks.get(i).getIndex()).contains(indexToSearch)) {
                    dockOut = dockOut + docks.get(i).toString() + "\n";
                }
            }
            out = dockOut;
        }
        //searches ships
        if (choice == 2) {
            for (int i = 0; i < ships.size(); i++) {
                if (Integer.toString(ships.get(i).getIndex()).contains(indexToSearch)) {
                    shipOut = shipOut + ships.get(i).toString() + "\n";
                }
            }
            out = shipOut;
        }
        //searches people
        if (choice == 3) {
            for (int i = 0; i < persons.size(); i++) {
                if (Integer.toString(persons.get(i).getIndex()).contains(indexToSearch)) {
                    peopleOut = peopleOut + persons.get(i).toString() + "\n";
                }
            }
            out = peopleOut;
        }
        //searches jobs
        if (choice == 4) {
            for (int i = 0; i < ships.size(); i++) {
                for (int j = 0; j < ships.get(i).getJobList().size(); j++) {
                    if (Integer.toString(ships.get(i).getJobList().get(j).getIndex()).contains(indexToSearch)) {
                        jobOut = jobOut + ships.get(i).getJobList().get(j).toString() + "\n";
                    }
                }
            }
            out = jobOut;
        }
        return out;

    }

    //assigns ships their jobs
    public void assignJobs(ArrayList<Job> worldJobs) {
        for (int i = 0; i < ships.size(); i++) {//for every ship
            for (int j = 0; j < worldJobs.size(); j++) {//for every job
                //if the ship is the parent to the job
                if (ships.get(i).getIndex() == worldJobs.get(j).getParentIndex()) {
                    ships.get(i).addJob(worldJobs.get(j));//add the job to the ship
                }
            }
        }
    }

    @Override
    public void run() {

        /*A pool with the same number of threads as the number of docks it has.
        This ensures the number of ships being worked on is the same as the number
        of docks this port has. One ship will be worked on per dock.*/
        ExecutorService portPool = Executors.newFixedThreadPool(docks.size());
        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i).getJobList().isEmpty() == false) {
                portPool.submit(ships.get(i));//submits ships to the pool with one thread
            } else {
                //the ship doesn't have a job so it is logged as such and can sail away
                JLabel noJobLabel = new JLabel("SHIP: " + ships.get(i).getName() + " " + ships.get(i).getIndex() + " HAS NO JOBS & HAS SAILED");
                jobBox.add(noJobLabel);
                jobBox.repaint();
            }
        }
        //waits for pool to finish to run code below
        portPool.shutdown();
        try {
            portPool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
        }
        JLabel portCompleteLabel = new JLabel("PORT: " + this.getName() + " " + this.getIndex() + " IS COMPLETE");
        jobBox.add(portCompleteLabel);
        jobBox.repaint();

    }
    //fills the skills arraylist

    public void fillSkills() {
        for (int i = 0; i < persons.size(); i++) {
            skills.add(persons.get(i).getSkill());
        }
    }
//returns the skills arraylist

    public ArrayList<String> getSkills() {
        return skills;
    }
//takes workers from the arraylist to work on jobs

    public synchronized Person pplTake(String skill) {
        Person workingPerson;//will be the return

        if (persons.isEmpty() == false) {

            for (int i = 0; i < persons.size(); i++) {//loop through the people at this port

                if (persons.get(i).getSkill().equals(skill)) {//if a person has the right skill
                    workingPerson = persons.get(i);//workingPerson=that skilled person

                    persons.remove(persons.get(i));//remove the person from the arraylist
                    return workingPerson;//send the person with the correct skill to Job

                }//end if

            }//end for loop

        }//end isEmpty()

        workingPerson = new Person(false);// fake person
        return workingPerson;
    }//end ppltake

//updates the Resource Pool of People in the GUI
    public synchronized void updatePersons() {
        String guiString = "";
        for (int i = 0; i < persons.size(); i++) {
            guiString = guiString + " " + persons.get(i).toString(true);
        }
        peopleLabel.setText(defaultLabel + "<br/>Available People: " + guiString + "<br/></html>");

    }
//adds workers to the arraylist to work on the next job

    public synchronized void pplAdd(Person usedPerson) {
//if they are not a blank person
        if (usedPerson.isPerson() == true) {
            persons.add(usedPerson);
        }//end if
    }//end pplAdd
}//end class
