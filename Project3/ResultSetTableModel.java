import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;
import com.mysql.cj.jdbc.MysqlDataSource;

/* 
Name: Avriel Lyon
Course: CNT 4714 Fall 2022 
Assignment title: Project 3 – A Two-tier Client-Server Application 
Date:  October 24, 2022 

Class:  ResultSetTableModel
*/ 

public class ResultSetTableModel extends AbstractTableModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int numberOfRows;
    
    private String DATABASE_URL = "jdbc:mysql://localhost:3306/operationslog?useTimezone=true&serverTimezone=UTC";
    private String USERNAME = "root";
    private String PASSWORD = "root";
    private Statement logStatement;

    public boolean connectedToDataBase = false;

    public ResultSetTableModel(Connection incomingConnection, String query) throws SQLException, ClassNotFoundException {
    	//set values
        connection = incomingConnection;
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        connectedToDataBase = true;
    }

	public Class getColumnClass(int column) throws IllegalStateException {
        if (!connectedToDataBase) {
            throw new IllegalStateException("Not Connected To Database");
        }
        try {
            String className = metaData.getColumnClassName(column + 1);
            return Class.forName(className);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Object.class;
    }

    public int getColumnCount() throws IllegalStateException {
        if (!connectedToDataBase) {
            throw new IllegalStateException("Not Connected To Database");
        }
        try {
            return metaData.getColumnCount();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return 0;
    }

    public String getColumnName(int column) throws IllegalStateException {
        if (!connectedToDataBase) {
            throw new IllegalStateException("Not Connected To Database");
        }
        try {
            return metaData.getColumnName(column + 1);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return "";
    }

/* 
Name: Avriel Lyon
Course: CNT 4714 Fall 2022 
Assignment title: Project 3 – A Two-tier Client-Server Application 
Date:  October 24, 2022 

Class:  ResultSetTableModel
*/ 

    public int getRowCount() throws IllegalStateException {
        if (!connectedToDataBase) {
            throw new IllegalStateException("Not Connected To Database");
        }

        return numberOfRows;
    }

    public Object getValueAt(int row, int column) throws IllegalStateException {
        if (!connectedToDataBase) {
            throw new IllegalStateException("Not Connected To Database");
        }

        try {
            resultSet.next();
            resultSet.absolute(row + 1);
            return resultSet.getObject(column + 1);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return "";
    }

    public void setQuery(String query) throws SQLException, IllegalStateException, ClassNotFoundException {
        if (!connectedToDataBase) {
            throw new IllegalStateException("Not Connected To Database");
        }

        resultSet = statement.executeQuery(query);

        metaData = resultSet.getMetaData();

        resultSet.last();
        numberOfRows = resultSet.getRow();
        
        //connect to database and pass in query 
        
        MysqlDataSource dataSource = null;

        dataSource = new MysqlDataSource();
        dataSource.setURL(DATABASE_URL);
        dataSource.setUser(USERNAME);
        dataSource.setPassword(PASSWORD);
        Connection logConnection = dataSource.getConnection();

        String logQuery = "update operationscount set num_queries = num_queries + 1";

        logStatement = logConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        logStatement.executeUpdate(logQuery);

        //table has changed
        
        fireTableStructureChanged();
    }

    public int setUpdate(String query) throws SQLException, IllegalStateException {
        if (!connectedToDataBase) {
            throw new IllegalStateException("Not Connected To Database");
        }

        int res = statement.executeUpdate(query);

        //connect to database and pass in query 
        
        MysqlDataSource dataSource = null;

        dataSource = new MysqlDataSource();
        dataSource.setURL(DATABASE_URL);
        dataSource.setUser(USERNAME);
        dataSource.setPassword(PASSWORD);
        Connection logConnection = dataSource.getConnection();

        String logQuery = "update operationscount set num_updates = num_updates + 1";

        logStatement = logConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        logStatement.executeUpdate(logQuery);
        
        //table has changed
        
        fireTableStructureChanged(); 
        
        return res;
    }

    public void disconnectFromDataBase() {
        if (!connectedToDataBase)
            return;
        else try {
            statement.close();
            connection.close();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            connectedToDataBase = false;
        }
    }
}