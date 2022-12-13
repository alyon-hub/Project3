import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import com.mysql.cj.jdbc.MysqlDataSource;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.Box;


/* 
Name: Avriel Lyon
Course: CNT 4714 Fall 2022 
Assignment title: Project 3 – A Two-tier Client-Server Application 
Date:  October 24, 2022 

Class:  SQLClientApp 
*/ 


public class SQLClientApp extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton ConnectButton, ClearQuery, ExecuteButton, ClearWindow;
    private JLabel QueryLabel, DetailsLabel, PropertiesLabel, UserLabel, PasswordLabel;
    private JTextArea TextQuery;
    private JComboBox PropertiesCombo;
    private JTextField UserText;
    private JPasswordField PasswordText;
    private JLabel StatusLabel, WindowLabel;
    private TableModel Empty;
    private JTable displayTable;
    private Connection connection;
    private ResultSetTableModel resultSetModel;

    public SQLClientApp() {
        String[] PropertiesItems = {
            "root.properties",
            "client.properties"
        };
        
        //create buttons
        ConnectButton = new JButton("Connect to DataBase");
        ExecuteButton = new JButton("Execute SQL Command");
        ClearQuery = new JButton("Clear SQL Command");
        ClearWindow = new JButton("Clear Result Window");

        //create labels
        QueryLabel = new JLabel("Enter An SQL Command");
        UserLabel = new JLabel("Username");
        PasswordLabel = new JLabel("Password");
        DetailsLabel = new JLabel("Connection Details");
        PropertiesLabel = new JLabel("Properties File");
        WindowLabel = new JLabel("SQL Execution Result Window");
        StatusLabel = new JLabel("**NOT CONNECTED**");

        //create text fields and combo
        PropertiesCombo = new JComboBox(PropertiesItems);
        TextQuery = new JTextArea(5, 5);
        UserText = new JTextField(10);
        PasswordText = new JPasswordField(10);

        //create empty table model
        Empty = new DefaultTableModel();

        //create displayTable
        displayTable = new JTable(Empty);
        displayTable.setModel(Empty);
        displayTable.setGridColor(Color.BLACK);
        
        //customize status label
        StatusLabel.setForeground(Color.RED);
        StatusLabel.setBackground(Color.BLACK);
        StatusLabel.setOpaque(true);

        setPreferredSize(new Dimension(905, 600));
        setLayout(null);

        JScrollPane scrollPane = new JScrollPane(TextQuery, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        TextQuery.setWrapStyleWord(true);
        TextQuery.setLineWrap(true);

        final Box square = Box.createHorizontalBox();
        square.add(displayTable);

        Box sqlSquare = Box.createHorizontalBox();
        sqlSquare.add(scrollPane);
        
        //set bounds for GUI components
        
        PropertiesCombo.setBounds(185, 45, 165, 25);
        
        ConnectButton.setBounds(20, 180, 165, 25);
        ExecuteButton.setBounds(650, 180, 165, 25);
        ClearWindow.setBounds(20, 540, 165, 25);
        ClearQuery.setBounds(450, 180, 165, 25);
        
        QueryLabel.setBounds(450, 10, 165, 25);
        UserLabel.setBounds(20, 85, 165, 25);
        PasswordLabel.setBounds(20, 115, 165, 25);
        DetailsLabel.setBounds(20, 10, 165, 25);
        PropertiesLabel.setBounds(20, 45, 165, 25);
        WindowLabel.setBounds(20, 260, 800, 25);
        StatusLabel.setBounds(20, 230, 850, 25);
        
        UserText.setBounds(185, 85, 165, 25);
        PasswordText.setBounds(185, 115, 165, 25);
        
        square.setBounds(20, 300, 850, 220);
        sqlSquare.setBounds(450, 42, 420, 125);
        
        
        //add GUI components 
        
        add(PropertiesCombo);
        
        add(ExecuteButton);
        add(ConnectButton);
        add(ClearQuery);    
        add(ClearWindow);
        
        add(QueryLabel);
        add(UserLabel);
        add(PasswordLabel);
        add(DetailsLabel);
        add(PropertiesLabel);
        add(WindowLabel);
        add(StatusLabel);

        
        add(UserText);
        add(PasswordText);  
        
        add(sqlSquare, BorderLayout.SOUTH);
        add(square, BorderLayout.NORTH);
        
        square.add(new JScrollPane(displayTable));

        ConnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    Properties properties = new Properties();
                    FileInputStream filein = null;
                    MysqlDataSource dataSource = null;

                    try {
                    	//loads file in and reads property values
                        filein = new FileInputStream(PropertiesCombo.getSelectedItem().toString());
                        properties.load(filein);
                        dataSource = new MysqlDataSource();
                        dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
                        dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
                        dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
                        
                        //convert to char array to compare with password char array
                        char[] ch = dataSource.getPassword().toCharArray();

                        //if both the username and password are correct, establish connection
                        if ((UserText.getText().equals(dataSource.getUser())) && (Arrays.equals(PasswordText.getPassword(), ch))) {
                            StatusLabel.setForeground(Color.GREEN);
                            StatusLabel.setText("Connected To: " + dataSource.getURL());
                            connection = dataSource.getConnection();
                            
                        } else {
                        	//alert user that incorrect credentials were entered
                        	StatusLabel.setText("NOT CONNECTED - User Credentials Do Not Match Properties File!");
                        }
                        
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);

                }
            }
        });

/* 
Name: Avriel Lyon
Course: CNT 4714 Fall 2022 
Assignment title: Project 3 – A Two-tier Client-Server Application 
Date:  October 24, 2022 

Class:  SQLClientApp 
*/ 

        ClearWindow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	//clears table
                displayTable.setModel(Empty);
            }
        });

        ClearQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	//clears query text
                TextQuery.setText("");
            }
        });

        ExecuteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    String query = TextQuery.getText();
                    
                    //if the query is to select, then set query, else set update
                    if (query.contains("select")) {
                    	
                        resultSetModel = new ResultSetTableModel(connection, TextQuery.getText());
                        resultSetModel.setQuery(query);
                        displayTable.setModel(resultSetModel);
                        
                    } else {
                    	
                        resultSetModel = new ResultSetTableModel(connection, TextQuery.getText());
                        JOptionPane.showMessageDialog(null, "Successful Update..." + resultSetModel.setUpdate(query) + " rows updated", "Successful Update", JOptionPane.PLAIN_MESSAGE);
                        
                    }

                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SQL Client App - (AL - CNT 4714 - FALL 2022 - Project 3)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new SQLClientApp());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}