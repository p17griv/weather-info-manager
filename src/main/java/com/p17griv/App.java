package com.p17griv;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Implements a GUI menu which allows users to access data about
 * various meteorological stations of USA, stored in a SQL Database.
 * 
 * @author	Pashalis Grivas
 * @version 1.0
 * @since   2020-01-03 
 */
public class App {

    private JFrame frame;
	private JPanel mainPanel;
	ArrayList<JPanel> panels;
	private CardLayout cl;
    private Database db;
    
    public App() {
        this.db = new Database();
		if(!db.establishDatabase()) {
			createErrorMessage("Unable to establish connection with database server.", "Error");
			System.exit(0);
		}
    }

	/**
	 * Creates application's window and a panel with CardLayout
	 * that contains other panels.
	 */
	public static void initialize() {
		//Create and set up the window.
		JFrame frame = new JFrame();
		frame.setResizable(false);
		frame.setSize(450, 300);
		frame.setTitle("Weather Info Manager"); // Define frame's title
		frame.setLocationRelativeTo(null); // Place frame in the center of the screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // Display the window

		App app = new App();
		app.frame = frame;

		app.mainPanel = new JPanel(new CardLayout()); // Create the panel that contains the "cards"
		app.panels = new ArrayList<JPanel>();
		for(int i=0; i<6; i++) {
			app.panels.add(new JPanel(null));
			if(i==0)
				app.mainPanel.add(app.panels.get(i), "Main Menu"); // Add panel for main menu to main panel
			else
				app.mainPanel.add(app.panels.get(i), "Query " + i); // Add query panel to main panel
		}
		app.cl = (CardLayout)(app.mainPanel.getLayout());
		app.frame.add(app.mainPanel); // Add panel to frame

		app.loadGUI();
	}

	/**
	 * Creates the GUI and displays it.
	 */
    public void loadGUI() {
		this.frame.revalidate();
		this.cl.show(this.mainPanel, "Main Menu"); // Display "Main Menu" panel

        final JLabel label = new JLabel("Main Menu"); // Create a label
		label.setFont(new Font("Tahoma", Font.BOLD, 14));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(0, 22, 444, 23);
		this.panels.get(0).add(label); // Add label to panel

		JButton createTablesBtn = new JButton("Create Tables");
        createTablesBtn.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
                if(db.tablesExist()) {
                    String[] options = {"No","Yes","Cancel"};
					int answer = JOptionPane.showOptionDialog(frame,
					    "Tables already exist. Do you want to delete them?",
					    "Tables Deletion",
					    JOptionPane.YES_NO_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null, // Do not use a custom Icon
					    options, // The titles of buttons
					    options[0]); // Default button
					if(answer == 1)
						if(db.deleteTables())
							createInfoMessage("Tables deleted successfully.", "Success");
						else
							createErrorMessage("Tables deletion failed", "Error");
				}
				else
                    if(db.createTables())
						createInfoMessage("Tables created successfully.", "Success");
					else
						createErrorMessage("Tables creation failed.", "Error");
            } 
          } );
		createTablesBtn.setBounds(115, 87, 220, 23);
		this.panels.get(0).add(createTablesBtn); // Add "createTablesBtn" button to panel

		JButton insertDataBtn = new JButton("Insert Data");
		insertDataBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(db.tablesExist())
					if(db.insertData())
						createInfoMessage("Data inserted successfully.", "Success");
					else
						createErrorMessage("Data insertion failed.", "Error");
				else
					createErrorMessage("Create tables first.", "Error");
			}
		});
		insertDataBtn.setBounds(115, 121, 220, 23);
		this.panels.get(0).add(insertDataBtn); // Add "insertDataBtn" button to panel

		final JButton backBtn = new JButton("Back");
		backBtn.setBounds(10, 11, 71, 23);

		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.first(mainPanel);
				label.setText("Main Menu"); // Change label text
				panels.get(0).add(label); // Add label to panel
			}
		});
		
		JLabel labelA = new JLabel("Choose a meteorological station:");
		labelA.setBounds(10, 59, 220, 14);
		this.panels.get(1).add(labelA);

		JScrollPane scrollPaneA2 = new JScrollPane();
		scrollPaneA2.setBounds(240, 84, 194, 176);
		this.panels.get(1).add(scrollPaneA2);
		
		final JTextPane resultTextPaneA = new JTextPane();
		resultTextPaneA.setEditable(false);
		scrollPaneA2.setViewportView(resultTextPaneA);
		
		final JScrollPane scrollPaneA1 = new JScrollPane();
		scrollPaneA1.setBounds(10, 84, 220, 176);
		this.panels.get(1).add(scrollPaneA1);

		JLabel labelB = new JLabel("Year:");
		labelB.setBounds(10, 61, 115, 23);
		this.panels.get(2).add(labelB);
		
		final JTextField textFieldB;
		textFieldB = new JTextField();
		textFieldB.setBounds(118, 62, 86, 20);
		this.panels.get(2).add(textFieldB);
		textFieldB.setColumns(10);
		
		final JButton insertBtnB = new JButton("Insert");
		insertBtnB.setBounds(218, 61, 109, 23);
		this.panels.get(2).add(insertBtnB);
		
		JScrollPane scrollPaneB = new JScrollPane();
		scrollPaneB.setBounds(10, 95, 424, 165);
		this.panels.get(2).add(scrollPaneB);
		
		final JTextPane resultTextPaneB = new JTextPane();
		resultTextPaneB.setEditable(false);
		scrollPaneB.setViewportView(resultTextPaneB); // Add result text to scrollPane

		JLabel labelC = new JLabel("Select a meteorological station:");
		labelC.setBounds(10, 59, 220, 14);
		this.panels.get(3).add(labelC);
		
		final JScrollPane scrollPaneC1 = new JScrollPane();
		scrollPaneC1.setBounds(10, 84, 220, 176);
		this.panels.get(3).add(scrollPaneC1);
		
		final JScrollPane scrollPaneC2 = new JScrollPane();
		scrollPaneC2.setBounds(240, 84, 194, 176);
		this.panels.get(3).add(scrollPaneC2);
		
		final JTextPane resultTextPaneC = new JTextPane();
		scrollPaneC2.setViewportView(resultTextPaneC); // Add result text to scrollPane

		JLabel labelD = new JLabel("Year");
		labelD.setBounds(10, 61, 115, 23);
		this.panels.get(4).add(labelD);
		
		final JTextField textFieldD;
		textFieldD = new JTextField();
		textFieldD.setColumns(10);
		textFieldD.setBounds(118, 62, 86, 20);
		this.panels.get(4).add(textFieldD);
		
		final JButton insertBtnD = new JButton("Insert");
		insertBtnD.setBounds(218, 61, 109, 23);
		this.panels.get(4).add(insertBtnD);
		
		JScrollPane scrollPaneD = new JScrollPane();
		scrollPaneD.setBounds(10, 95, 424, 165);
		this.panels.get(4).add(scrollPaneD);
		
		final JTextPane resultTextPaneD = new JTextPane();
		resultTextPaneD.setEditable(false);
		scrollPaneD.setViewportView(resultTextPaneD); // Add result text to scrollPane

		JScrollPane scrollPaneE = new JScrollPane();
		scrollPaneE.setBounds(10, 58, 424, 202);
		this.panels.get(5).add(scrollPaneE);
		
		final JTextPane resultTextPaneE = new JTextPane();
		resultTextPaneE.setEditable(false);
		scrollPaneE.setViewportView(resultTextPaneE); // Add result text to scrollPane
		
		JButton queriesBtn = new JButton("Queries");
		queriesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(db.tablesExist() && !db.tablesEmpty()) {
					final String[] queries = { "Get information about a meteorological station.", 
					"Get all stations that recorded the maximum and the minimum temperature of a specific year.", 
					"Get the average of maximum and minimum temperatures of a station.", 
					"Get information about the winds recorded by each station on a specific year.", 
					"Get information about the longest-lasted winds recorded by each station." };
				    String selectedQuery = (String) JOptionPane.showInputDialog(frame, 
				        "Select a query:",
				        "Query Selection",
				        JOptionPane.QUESTION_MESSAGE, 
				        null, 
				        queries, 
				        queries[0]);
					
				    if(selectedQuery!=null) {
				    	if(selectedQuery.equals("Get information about a meteorological station.")) {
							cl.show(mainPanel, "Query 1"); // Display "Query 1" panel
							label.setText("Query 1"); // Change label text
							panels.get(1).add(label); // Add label to panel
							panels.get(1).add(backBtn); // Add back button

							final JList metStations = new JList(db.getAllMetStations().toArray());
							scrollPaneA1.setViewportView(metStations); // Add list to scrollPane
							metStations.addListSelectionListener(new ListSelectionListener() {
								public void valueChanged(ListSelectionEvent evt) {
									String choice = (String) metStations.getSelectedValue();
									String metStation = choice.substring(0, choice.indexOf(",")); // Keep only the name of the station
									resultTextPaneA.setText(db.getMetStation(metStation));
									resultTextPaneA.setCaretPosition(0); // Start from the top
								}
							});
						}
					    else if(selectedQuery.equals("Get all stations that recorded the maximum and the minimum temperature of a specific year.")) {
							cl.show(mainPanel, "Query 2"); // Display "Query 1" panel
							label.setText("Query 2"); // Change label text
							panels.get(2).add(label); // Add label to panel
							panels.get(2).add(backBtn); // Add back button

							insertBtnB.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									try {
										int input = Integer.parseInt(textFieldB.getText());
										if(input < 0 || input > 9999)
											throw new NumberFormatException("error");
										resultTextPaneB.setText(db.getYearsMaxAndMinTemp(input));
										resultTextPaneB.setCaretPosition(0); // Start from the top
									}
									catch(java.lang.NumberFormatException ex) {
										createErrorMessage("Invalid year value.", "Error");
							    	}
								}
							});
						}
					    else if(selectedQuery.equals("Get the average of maximum and minimum temperatures of a station.")) {
							cl.show(mainPanel, "Query 3"); // Display "Query 1" panel
							label.setText("Query 3"); // Change label text
							panels.get(3).add(label); // Add label to panel
							panels.get(3).add(backBtn); // Add back button

							final JList metStations = new JList(db.getAllMetStations().toArray());
							scrollPaneC1.setViewportView(metStations);
							metStations.addListSelectionListener(new ListSelectionListener() {
					            public void valueChanged(ListSelectionEvent evt) {
					            	String choice = (String) metStations.getSelectedValue();
					            	String metStation = choice.substring(0, choice.indexOf(",")); //keep only the name
					            	resultTextPaneC.setText(db.getStationsMaxMinAvgTemp(metStation));
					            }
					        });
						}
					    else if(selectedQuery.equals("Get information about the winds recorded by each station on a specific year.")) {
							cl.show(mainPanel, "Query 4"); // Display "Query 1" panel
							label.setText("Query 4"); // Change label text
							panels.get(4).add(label); // Add label to panel
							panels.get(4).add(backBtn); // Add back button

							insertBtnD.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									try { 
										int input = Integer.parseInt(textFieldD.getText());
										if(input < 0 || input > 9999)
											throw new NumberFormatException("error");
										resultTextPaneD.setText(db.getYearsWindInfo(input));
									}
									catch(java.lang.NumberFormatException ex) {
										createErrorMessage("Invalid year value.", "Error");
							    	}
								}
							});
						}
					    else if(selectedQuery.equals("Get information about the longest-lasted winds recorded by each station.")) {
							cl.show(mainPanel, "Query 5"); // Display "Query 1" panel
							label.setText("Query 5"); // Change label text
							panels.get(5).add(label); // Add label to panel
							panels.get(5).add(backBtn); // Add back button

							resultTextPaneE.setText(db.getLongestWindPerMetStation());
							resultTextPaneE.setCaretPosition(0); // Start from the top
						}
				    }
				}
				else
					JOptionPane.showMessageDialog(frame,
		            	    "Make sure that you have created tables and that you have inserted the data.",
		            	    "Error",
		            	    JOptionPane.ERROR_MESSAGE);
			}
		});
		queriesBtn.setBounds(115, 155, 220, 23);
		this.panels.get(0).add(queriesBtn); // Add "queriesBtn" button to panel
		
		JButton exitBtn = new JButton("Exit");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] options = {"No","Yes"};
				int answer = JOptionPane.showOptionDialog(frame,
				    "Are you sure you want to exit?",
				    "Exit",
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null, // Do not use a custom Icon
					options, // The titles of buttons
					options[0]); // Default button
				if(answer == 1)
					System.exit(0);
			}
		});
		exitBtn.setBounds(115, 189, 220, 23);
		this.panels.get(0).add(exitBtn); // Add "exitBtn" button to panel
    }

	/**
	 * Displays an error message dialog.
	 * 
	 * @param message 	The message of the error.
	 * @param title		The title of the dialog box.
	 */
	public void createErrorMessage(String message, String title) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Displays an information message dialog.
	 * 
	 * @param message 	The message of the dialog.
	 * @param title		The title of the dialog box.
	 */
	public void createInfoMessage(String message, String title) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main( String[] args ) {
		try {
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Set System L&F
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
				initialize();
            }
        });
	}
}
