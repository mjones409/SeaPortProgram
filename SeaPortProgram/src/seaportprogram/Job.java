/*
 * File: Job.java
 * Author: Marcus Jones
 * Date: 12 October 2019
 * Purpose: CMSC 335 Project 4
 */
package seaportprogram;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class Job extends Thing implements Runnable {

    private double duration;
    private ArrayList<String> requirements = new ArrayList<>();
    private Box jobBox;//need this to add jpanels to it
    private JButton jbGo = new JButton("Stop");//stop button
    private JButton jbKill = new JButton("Cancel");//cancel button
    private JProgressBar jobBar = new JProgressBar();//loading bar
    private boolean goFlag = true, noKillFlag = true;//flags
    private SeaPort parentPort;//port this job belongs to
    private Dock parentDock;//dock this job belongs to
    private Ship parentShip;//ship this job belongs to
    //people currently working on this job
    private ArrayList<Person> pplWorking = new ArrayList<>();
    private String explain = " Port didn't have the requirements so ";

    Status status = Status.SUSPENDED;

    enum Status {
        RUNNING, SUSPENDED, CANT, DONE, WORKERS
    }

    @Override
    public String toString() {
        return "Job: " + super.toString() + " Duraion: " + duration + " Requirements: " + Arrays.toString(requirements.toArray());
    }

    public Job(Scanner sc, Box jobBox, HashMap hmThings) {
        super(sc);
        jobBar.setStringPainted(true);
        this.jobBox = jobBox;
        //parsing file to get relevant info
        duration = sc.nextDouble();
        String thisLine = sc.nextLine();
        Scanner jobScan = new Scanner(thisLine);
        while (jobScan.hasNext()) {
            requirements.add(jobScan.next());//adds reqs to the arraylist
        }
        /*getting the parent port in order to check the 
        skills at that port in the run method*/
        parentShip = (Ship) hmThings.get(this.getParentIndex());

        if (hmThings.get(parentShip.getParentIndex()) instanceof SeaPort) {
            parentPort = (SeaPort) hmThings.get(parentShip.getParentIndex());
        }

        if (hmThings.get(parentShip.getParentIndex()) instanceof Dock) {
            parentDock = (Dock) hmThings.get(parentShip.getParentIndex());
            parentPort = (SeaPort) hmThings.get(parentDock.getParentIndex());
        }

        //go / suspend listener
        jbGo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleGoFlag();
            }
        });
        //kill button listener
        jbKill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setKillFlag();
                jobBox.repaint();
            }
        });

    }

    public SeaPort getParentPort() {
        return parentPort;
    }

    @Override
    public void run() {

        /*change milliseconds to seconds
        I recommend deleting this line during testing
        beacuse the jobs take a very long time to complete
        when their times are changed to seconds instead of ms*/
        double durationSecs = duration * 1000;
        //panel to add components to
        JPanel jobStartPanel = new JPanel();
        JLabel jobStartLabel = new JLabel(this.getName() + " " + this.getIndex()
                + " has started. " + "Skills needed: " + Arrays.toString(requirements.toArray()));
        showStatus(Status.WORKERS);
        jobStartPanel.add(jobBar);
        jobStartPanel.add(jbGo);
        jobStartPanel.add(jbKill);
        jobStartPanel.add(jobStartLabel);
        jobBox.add(jobStartPanel);//add panel with all components to the box
        jobBox.repaint();//updates the box


        /*Checks to see if this job can be run at all.
                If the port doesn't have the reqs, the user will be notified 
            why it can't be run and the job will not start*/
        boolean requirementsMet = true;
        for (int i = 0; i < requirements.size(); i++) {
            if (Collections.frequency(parentPort.getSkills(), requirements.get(i))
                    < Collections.frequency(requirements, requirements.get(i))) {
                requirementsMet = false;
            }//end if
        }//end for
        //sets the kill flag if reqs not met
        if (requirementsMet == false) {
            setKillFlag();
        }
        //looks for workers to fill the job if any reqs exist
        if (requirementsMet == true && requirements.isEmpty() == false) {
            showStatus(Status.WORKERS);

            //while we don't have all the workers we require
            while (requirements.size() != pplWorking.size()) {

                try {
                    /*sleeps so each job doesn't have to go through
                    the loop so many times waiting for workers*/
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                //add people to our pplWorking Arraylist
                for (int i = 0; i < requirements.size(); i++) {
                    pplWorking.add(parentPort.pplTake(requirements.get(i)));
                }//end for loop

                /*checks to see if we got a blank person,
        meaning that the person we were trying to get
        is already busy on a job*/
                boolean hasAllPeople = true;
                for (int i = 0; i < pplWorking.size(); i++) {
                    //checks to see if real person or not
                    if (pplWorking.get(i).isPerson() == false) {
                        hasAllPeople = false;
                    }//end if
                }//end for loop
                if (hasAllPeople == false) {//if we added a blank person,
                    for (int i = 0; i < pplWorking.size(); i++) {//release all pplWorking
                        parentPort.pplAdd(pplWorking.get(i));
                    }//end for
                    pplWorking.clear();
                }//end if
            }//end while

        }/*end if. job has all the workers it needs*/

        //tells the user which people have been selected for the job
        jobStartLabel.setText("<html>" + jobStartLabel.getText() + "<br/>People working on this job: " + Arrays.toString(pplWorking.toArray()) + "</html>");
        jobBox.repaint();
        /*durationSecs is the time that needs to be worked an timeWorked 
        is the time that has actually been worked so far*/
        double timeWorked = 0;//time this job has actually been worked on
        parentPort.updatePersons();//updates the GUI for persons
        while (durationSecs > timeWorked && noKillFlag) {
            try {
                explain = "";
                Thread.sleep(100);//working for 100ms
            } catch (InterruptedException e) {
            }
            if (goFlag) {
                showStatus(Status.RUNNING);
                timeWorked += 100;//increments the time worked
                //sets the jobBar to the percent of time that has been worked
                jobBar.setValue((int) ((timeWorked / durationSecs) * 100));
            } else {
                showStatus(Status.SUSPENDED);
            }//end if stepping
        }//end while
        //shows that the job has ended
        showStatus(Status.DONE);
        JLabel jobEndLabel = new JLabel("<html>" + explain + this.getName() + " " + this.getIndex() + " has ended. Parent:" + this.getParentIndex()
                + "workers released: " + "<br/>" + Arrays.toString(pplWorking.toArray()) + "<br/><br/></html>");
        jobBox.add(jobEndLabel);
        jobBox.repaint();

        if (requirementsMet == false) {
            showStatus(Status.CANT);//shows status if reqs not met
        }
        //release all pplWorking because the job is done
        for (int i = 0; i < pplWorking.size(); i++) {
            parentPort.pplAdd(pplWorking.get(i));
        }//end for
        pplWorking.clear();
        jobBox.repaint();//updates the box
        parentPort.updatePersons();//updates the GUI for persons

    }//end run

    public void toggleGoFlag() {//can be go or no go
        goFlag = !goFlag;
    }

    public void setKillFlag() {//kills the job and lets user know
        if (noKillFlag == true) {
            noKillFlag = false;
            jbKill.setBackground(Color.red);
            JLabel jobKillLabel = new JLabel(explain + this.getName() + " " + this.getIndex() + " has been CANCELED. Parent:" + this.getParentIndex());
            jobBox.add(jobKillLabel);
            jobBox.repaint();
        }
    }

    //switch to show the status of the jobs
    void showStatus(Status st) {
        status = st;
        switch (status) {
            case RUNNING:
                jbGo.setBackground(Color.green);
                jbGo.setText("Running");
                break;
            case SUSPENDED:
                jbGo.setBackground(Color.yellow);
                jbGo.setText("Suspended");
                break;
            case CANT:
                jbGo.setBackground(Color.orange);
                jbGo.setText("Job Reqs Can't Be Met");
                break;
            case DONE:
                jbGo.setBackground(Color.red);
                jbGo.setText("Done");
                break;
            case WORKERS:
                jbGo.setBackground(Color.cyan);
                jbGo.setText("Waiting for workers");
                break;
        } // end switch on status 
    } // end showStatus
}
