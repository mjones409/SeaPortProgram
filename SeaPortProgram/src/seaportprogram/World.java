/*
 * File: World.java
 * Author: Marcus Jones
 * Date: 12 October 2019
 * Purpose: CMSC 335 Project 4
 */
package seaportprogram;

import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.tree.*;

@SuppressWarnings("unchecked")
public class World extends Thing implements Runnable {

//box that all the job GUI will be added to
    private Box jobBox = Box.createVerticalBox();
//box that all the GUI for people updates will be added to
    private Box peopleBox = Box.createVerticalBox();
// hashmap of everything by index
    private HashMap<Integer, Thing> hmThings = new HashMap<>();
// Create an ArrayList object to hold ports 
    private ArrayList<SeaPort> ports = new ArrayList<>();
// node to help make the JTree
    private DefaultMutableTreeNode node = new DefaultMutableTreeNode("Ports");
    //arraylist of jobs that need to be assigned
    private ArrayList<Job> worldJobs = new ArrayList<>();
    //makes sure you only add the skills to each port once
    private boolean hasAddedSkills = false;

    public World(Scanner sc, int sortSelection, Box jobBox, Box peopleBox) {
        //call to Thing (no args)
        super();
        this.jobBox = jobBox;//makes the private jobBox equal to the parameter
        this.peopleBox = peopleBox;//makes the private peopleBox equal to the parameter
        while (sc.hasNextLine() && sc.hasNext()) {//while loop
            //switch statement
            switch (sc.next()) {
                case "port":
                    addPort(sc, hmThings);
                    break;
                case "dock":
                    addDock(sc, hmThings);
                    break;
                case "cship":
                    addCShip(sc, hmThings);
                    break;
                case "pship":
                    addPShip(sc, hmThings);
                    break;
                case "person":
                    addPerson(sc, hmThings);
                    break;
                case "job":
                    //adds skills to the ports before jobs are created
                    if (hasAddedSkills == false) {
                        for (int i = 0; i < ports.size(); i++) {
                            ports.get(i).fillSkills();
                        }
                        hasAddedSkills = true;
                    }
                    addJob(sc, hmThings);//add jobs
                    break;

                default:
                    if (sc.hasNextLine()) {
                        sc.nextLine();
                    }
                    break;
            }//end switch
        }//end while

        //sort sea ports by name
        Collections.sort(ports);
        showWorld(sortSelection);

    }

    @SuppressWarnings("unchecked")
    private void addJob(Scanner sc, HashMap hmThings) {
        Job theJob = new Job(sc, jobBox, hmThings);
        worldJobs.add(theJob);
        hmThings.put(worldJobs.get(worldJobs.size() - 1).getIndex(), worldJobs.get(worldJobs.size() - 1));
        theJob.getParentPort().assignJobs(worldJobs);//assigns jobs to ports

    }

    @SuppressWarnings("unchecked")
    private void addPort(Scanner sc, HashMap hmThings) {
        ports.add(new SeaPort(sc, jobBox, peopleBox));
        hmThings.put(ports.get(ports.size() - 1).getIndex(), ports.get(ports.size() - 1));
    }

    @SuppressWarnings("unchecked")
    private void addDock(Scanner sc, HashMap hmThings) {
        Dock theDock = new Dock(sc);

        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).getIndex() == theDock.getParentIndex()) {
                ports.get(i).assignDock(theDock, hmThings);
            }
        }

    }

    @SuppressWarnings("unchecked")
    private void addCShip(Scanner sc, HashMap hmThings) {
        CargoShip theCShip = new CargoShip(sc, jobBox);
        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).getIndex() == theCShip.getParentIndex()) {
                ports.get(i).assignShip(theCShip, hmThings);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addPShip(Scanner sc, HashMap hmThings) {
        boolean pAssigned = false;
        PassengerShip thePShip = new PassengerShip(sc, jobBox);
        for (int i = 0; i < ports.size(); i++) {

            if (ports.get(i).assignPShip(thePShip, hmThings) == true) {
                pAssigned = true;
            }
        }
        if (pAssigned == false) {
            Scanner shipScan = new Scanner(thePShip.toString());
            shipScan.next();
            shipScan.next();
            addCShip(shipScan, hmThings);

        }
        //if it belongs to a port but no dock
        if (hmThings.containsKey(thePShip.getIndex()) == false) {
            for (int i = 0; i < ports.size(); i++) {
                if (ports.get(i).getIndex() == thePShip.getParentIndex()) {
                    ports.get(i).assignShip(thePShip, hmThings);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addPerson(Scanner sc, HashMap hmThings) {
        Person thePerson = new Person(sc);
        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).getIndex() == thePerson.getParentIndex()) {
                ports.get(i).assignPerson(thePerson, hmThings);
            }
        }

    }

    //shows everything
    public String showWorld(int sortSelection) {
        node = new DefaultMutableTreeNode("Ports");
        String builtWorld = "";

        //used for showWorld()
        for (int i = 0; i < ports.size(); i++) {
            ports.get(i).sortShips(sortSelection);
            builtWorld = builtWorld + ports.get(i).toString()
                    + "====================================================="
                    + "====================================================\n\n";
            //adding nodes
            DefaultMutableTreeNode portNode = new DefaultMutableTreeNode(ports.get(i).getName() + " (" + ports.get(i).getIndex() + ")");
            portNode = ports.get(i).getNodes(portNode);
            node.add(portNode);
        }
        return builtWorld;
    }

    //searches by name
    public String searchName(String userSearch) {
        String jobOut = "";
        String peopleOut = "";
        String shipOut = "";
        String dockOut = "";
        String portOut = "Ports With That Name:";
        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).getName().toLowerCase().contains(userSearch.toLowerCase())) {
                portOut = portOut + "\nPort: " + ports.get(i).getName() + " " + ports.get(i).getIndex() + "";
            }
            dockOut = dockOut + ports.get(i).searchMoreNames(1, userSearch);
            shipOut = shipOut + ports.get(i).searchMoreNames(2, userSearch);
            peopleOut = peopleOut + ports.get(i).searchMoreNames(3, userSearch);
            jobOut = jobOut + ports.get(i).searchMoreNames(4, userSearch);

        }

        return portOut + "\n\nDocks With That Name: \n" + dockOut + "\nShips With That Name:\n" + shipOut + "\nPeople With That Name:\n" + peopleOut + "\nJobs With That Name:\n" + jobOut;
    }

    //searches by index
    public String searchIndex(String userSearch) {
        String portOut = "Ports With That Index: \n";
        String peopleOut = "";
        String shipOut = "";
        String dockOut = "";
        String jobOut = "";

        for (int i = 0; i < ports.size(); i++) {
            if (Integer.toString(ports.get(i).getIndex()).contains(userSearch)) {
                portOut = portOut + "Port: " + ports.get(i).getName() + " " + ports.get(i).getIndex() + "\n";
            }
            dockOut = dockOut + ports.get(i).indexSearch(1, userSearch);
            shipOut = shipOut + ports.get(i).indexSearch(2, userSearch);
            peopleOut = peopleOut + ports.get(i).indexSearch(3, userSearch);
            jobOut = jobOut + ports.get(i).indexSearch(4, userSearch);
        }

        return portOut + "\nDocks With That Index: \n" + dockOut + "\nShips With That Index:\n" + shipOut + "\nPeople With That Index:\n" + peopleOut + "\nJobs With That Index:\n" + jobOut;
    }

    //searches by skill
    public String searchSkill(String userSearch) {
        String peopleOut = "";
        for (int i = 0; i < ports.size(); i++) {
            peopleOut = peopleOut + ports.get(i).skillSearch(userSearch);
        }
        return peopleOut;
    }

    //exact index search that searches the hashmap for indexes
    @SuppressWarnings("unchecked")
    public String hmSearch(String userSearch) {

        int index;
        try {
            index = Integer.parseInt(userSearch);
            if (hmThings.get(index) instanceof SeaPort) {
                return "SeaPort: " + hmThings.get(index).getName() + " " + hmThings.get(index).getIndex();
            } else {
                return hmThings.get(index).toString();
            }
        } catch (Exception e) {
            return "Nothing Found.\n'" + userSearch + "' is not a valid index.";
        }
    }

    public DefaultMutableTreeNode getTree() {
        return node;
    }

    @Override
    public void run() {
        //thread pool of ports in this world
        ExecutorService worldPool = Executors.newFixedThreadPool(ports.size());
        for (int i = 0; i < ports.size(); i++) {
            worldPool.submit(ports.get(i));//submits each port
        }
        //nothing can get executed past this until the executor service is done
        worldPool.shutdown();
        try {
            worldPool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
        }
        //all the jobs are done
        JLabel completeLabel = new JLabel("ALL SHIPS HAVE SAILED");
        jobBox.add(completeLabel);//shows label
        jobBox.repaint();//repaints the box

    }

}
