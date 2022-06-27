/*
 * File: SeaPortProgram.java
 * Author: Marcus Jones
 * Date: 12 October 2019
 * Purpose: CMSC 335 Project 4
 */
package seaportprogram;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class SeaPortProgram extends JFrame {

    private static World world;

    public static void main(String[] args) {
        //used for JComboBox
        String[] comboSelections = new String[]{"Show All", "Name", "Index", "Skill", "Exact Index Search"};
        String[] sortSelections = new String[]{"Name", "Weight", "Length", "Width", "Draft"};
        //creating all the panels and jframe
        JFrame f0 = new JFrame("Sea Port Program");
        JPanel p0 = new JPanel(new BorderLayout());
        JPanel fileNSearchPanel1 = new JPanel();
        fileNSearchPanel1.setLayout(new BorderLayout());
        JPanel filePanel2 = new JPanel();
        JPanel searchPanel3 = new JPanel();
        JPanel searchP4 = new JPanel();
        JPanel searchP5 = new JPanel();
        JLabel sortLabel = new JLabel("Sort Ships By: ");
        JPanel treePanel = new JPanel(new BorderLayout());

        //creating gui components
        JButton chooseFileButton = new JButton("Choose File");
        JTextField filePath = new JTextField(45);//the file path & file name
        filePath.setText("File Path...");
        filePath.setEditable(false);
        JButton searchButton = new JButton("Search");
        JComboBox<String> fieldDropdown = new JComboBox<>(comboSelections);
        JComboBox<String> sortDropdown = new JComboBox<>(sortSelections);
        JTextField searchBox = new JTextField(35);//search box
        searchBox.setText("Search...");

        treePanel.setPreferredSize(new Dimension(250, 1000));
        treePanel.add(new JLabel("Tree"), BorderLayout.NORTH);

        JTextArea jta = new JTextArea(0, 175);
        jta.setEditable(false);
        JScrollPane jsp = new JScrollPane(jta);
        jta.setText("Search and Summary Results");

        //making gui look pretty
        jta.setFont(new java.awt.Font("Monospaced", 0, 15));
        jta.setBackground(Color.WHITE);
        searchPanel3.setBackground(Color.DARK_GRAY);
        searchP4.setBackground(Color.DARK_GRAY);
        searchP5.setBackground(Color.DARK_GRAY);
        filePanel2.setBackground(Color.DARK_GRAY);
        sortLabel.setForeground(Color.WHITE);
        treePanel.setBackground(Color.LIGHT_GRAY);
        //set up filechooser
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new java.io.File("."));
        fc.setDialogTitle("Choose Your Sea Port File");

        //job gui
        //box to hold all the job status updates
        Box jobBox = Box.createVerticalBox();
        JPanel jobPanel = new JPanel();
        jobPanel.add(jobBox);
        JScrollPane jobScroll = new JScrollPane(jobPanel);
        //jobScroll.setPreferredSize(new Dimension(1000, 350));
        jobScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jobScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jobBox.add(new JLabel("Status of Jobs"));

        //show people in real time
        Box peopleBox = Box.createVerticalBox();
        JPanel peoplePanel = new JPanel();
        JScrollPane peopleScroll = new JScrollPane(peoplePanel);
        peopleScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        peopleScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        peoplePanel.add(peopleBox);
        peopleBox.add(new JLabel("Resource Pool of People"));

        //JSplitPanes
        //sideSplit
        JSplitPane sideSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sideSplit.setTopComponent(treePanel);
        sideSplit.setBottomComponent(jsp);
        sideSplit.setDividerLocation(250);

        //bottomSplit
        JSplitPane bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        bottomSplit.setPreferredSize(new Dimension(1000, 300));
        bottomSplit.setTopComponent(peopleScroll);
        bottomSplit.setBottomComponent(jobScroll);
        bottomSplit.setDividerLocation(150);
        //topSplit
        JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        topSplit.setPreferredSize(new Dimension(1000, 300));
        topSplit.setTopComponent(p0);
        topSplit.setBottomComponent(bottomSplit);
        topSplit.setDividerLocation(500);

        //panels are added here
        searchP5.add(searchBox, BorderLayout.CENTER);
        searchP4.add(searchButton, BorderLayout.WEST);
        searchP4.add(fieldDropdown, BorderLayout.EAST);
        searchP5.add(sortLabel, BorderLayout.SOUTH);
        searchP5.add(sortDropdown, BorderLayout.EAST);
        searchPanel3.add(searchP4, BorderLayout.WEST);
        searchPanel3.add(searchP5, BorderLayout.EAST);
        filePanel2.add(chooseFileButton, BorderLayout.EAST);
        filePanel2.add(filePath, BorderLayout.CENTER);
        fileNSearchPanel1.add(filePanel2, BorderLayout.NORTH);
        fileNSearchPanel1.add(searchPanel3, BorderLayout.SOUTH);
        p0.add(fileNSearchPanel1, BorderLayout.NORTH);
        p0.add(sideSplit, BorderLayout.CENTER);
        f0.add(topSplit, BorderLayout.CENTER);
        f0.setSize(1200, 1000);
        f0.setVisible(true);
        f0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //choose file button action listener
        chooseFileButton.addActionListener((ActionEvent arg1) -> {
            world = null;
            fc.showOpenDialog(chooseFileButton);// opens choosefile
            jobBox.removeAll();//resets the job update GUI when a new file is selected
            peopleBox.removeAll();//resets the people update GUI when a new file is selected
            try {
                //scanner equals the chosen file contents
                Scanner sc = new Scanner(new File(fc.getSelectedFile().getName()));
                //sets the file path in the text box
                filePath.setText(fc.getSelectedFile().getAbsolutePath());

                //create new World class passing sc
                world = new World(sc, sortDropdown.getSelectedIndex(), jobBox, peopleBox);

                //displays all the objects in the world
                jta.setText(world.showWorld(sortDropdown.getSelectedIndex()));
                //scrolls gui up
                jta.setCaretPosition(0);

                //tree
                treePanel.removeAll();//resets tree
                JTree tree = new JTree(world.getTree());//gets tree
                JScrollPane treeJSP = new JScrollPane(tree);//scrollpane
                treePanel.add(treeJSP);//adds tree jsp to panel
                //resets the GUI of the tree each time a new file is selected
                treePanel.setVisible(false);
                treePanel.setVisible(true);

            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                JOptionPane.showMessageDialog(f0,
                        "The chosen file is not formatted correctly, please choose a different file. ");

            }
            /*starts doing the job in a separate thread from main so that you 
        can still use the GUI while the jobs are being done
             */
            Thread startWork = new Thread(world);
            startWork.start();
        });//end choose file button action listener

        //search button action listener
        searchButton.addActionListener((ActionEvent arg1) -> {
            //Show All is selected
            if (fieldDropdown.getSelectedIndex() == 0) {
                jta.setText(world.showWorld(sortDropdown.getSelectedIndex()));
                jta.setCaretPosition(0);
            }
            //search by name is selected
            if (fieldDropdown.getSelectedIndex() == 1) {
                jta.setText(world.searchName(searchBox.getText()));
                jta.setCaretPosition(0);
            }
            //search by index is selected
            if (fieldDropdown.getSelectedIndex() == 2) {
                jta.setText(world.searchIndex(searchBox.getText()));
                jta.setCaretPosition(0);
            }
            //search by skill is selected.
            if (fieldDropdown.getSelectedIndex() == 3) {
                jta.setText(world.searchSkill(searchBox.getText()));
                jta.setCaretPosition(0);
            }
            //Search Exact Index is selected
            if (fieldDropdown.getSelectedIndex() == 4) {
                jta.setText(world.hmSearch(searchBox.getText()));
                jta.setCaretPosition(0);
            }
        });//end search button action listener

    }

}
