package com.morsiani.bench.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.morsiani.bench.config.ConfigHelper;

/**
 * classe con i metodi per eseguire gli statements su PostgreSQL. 
 * 1. apre/chiude connessioni al database, 
 * 2. crea la tabella di test,
 * 3. esegue insert-batch statements. 
 * 4. esegue select statements.
 * 
 * Per eseguire i due tipi di statements, fornisce 4 metodi per tipo: 
 * a. uno per praparare lo statement, 
 * b. uno per settare i dati , 
 * c. uno per eseguirlo e fare la commit, 
 * d. e uno per chiudere lo statement.
 * 
 */
public class DBHelper {

	// nome tabella e nome dei suoi attributi

	/**
	 * nome tabella
	 */
	protected static final String TABLE_NAME = "test";

	/**
	 * nome attributo PK
	 */
	protected static final String COLUMN_PK_NAME = "ATT_ID";

	/**
	 * nome attributo di tipo decimal
	 */
	protected static final String COLUMN_DECIMAL_NAME = "ATT_DECIMAL";

	/**
	 * nome attributo di tipo int
	 */
	protected static final String COLUMN_INT_NAME = "ATT_INT";

	/**
	 * nome attributo di tipo varchar
	 */
	protected static final String COLUMN_VARCHAR_NAME = "ATT_VARCHAR";

	/**
	 * nome attributo di tipo date
	 */
	protected static final String COLUMN_DATE_NAME = "ATT_DATE";

	protected Connection connection;
	protected String username;
	protected String password;
	protected String serverName;
	protected String databaseName;
	protected String portNumber;

	private PreparedStatement insertBatchPreparedStatement;
	private PreparedStatement selectPreparedStatement;

	public DBHelper() {
		// paramentri di connessione da config file
		ConfigHelper helper = new ConfigHelper();
		serverName = helper.getServerName();
		portNumber = helper.getServerPortNumber();
		databaseName = helper.getDatabaseName();
		username = helper.getUsername();
		password = helper.getPassword();

	}

	
	/**
	 * apre una connessione al DB
	 * @return 0 se la conn e' ok. valore negativo se si e' verificato un errore. 
	 **/
	public int connect() {

		if (connection != null)
			return 0;

		try {
			String connectionUrl = "jdbc:postgresql://" + serverName + ":" + portNumber + "/" + databaseName;

			// stabilisce la conn.
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(connectionUrl, username, password);

			return 0;

		} catch (ClassNotFoundException | SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);

			return -1;
		}

	}
	
	/**
	 * crea la tabella di test (prima la droppa se esiste).
	 * la tabella ha le colonne:
	 * - COLUMN_PK_NAME 		integer not null PK 
	 * - COLUMN_DECIMAL_NAME 	decimal not null column 
	 * - COLUMN_INT_NAME 		integer not null column 
	 * - COLUMN_VARCHAR_NAME 	varchar not null column 
	 * - COLUMN_DATE_NAME 		date not null column 
	 * 
	 * @return 0 se la conn e' ok. un numero negativo in caso di errore.
	 */
	public int createTable() {
		int result = 0;

		String dropTableSQL = "  DROP TABLE IF EXISTS " + TABLE_NAME;

		try (PreparedStatement dropTablePreparedStatement = connection.prepareStatement(dropTableSQL)) {

			connection.setAutoCommit(true);
			// esegue drop table
			dropTablePreparedStatement.executeUpdate();

		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
			result = -1;
		}

		if (result < 0)
			return result;

		String createTableSQL = "CREATE TABLE " + TABLE_NAME 
				+ "(" + COLUMN_PK_NAME + " SERIAL PRIMARY KEY, "
				+ COLUMN_DECIMAL_NAME + " DECIMAL(9,2) NOT NULL, " 
				+ COLUMN_INT_NAME + " INTEGER NOT NULL, "
				+ COLUMN_VARCHAR_NAME + " VARCHAR(20) NOT NULL, " 
				+ COLUMN_DATE_NAME + " DATE NOT NULL " + ")";

		try (PreparedStatement createTablePreparedStatement = connection.prepareStatement(createTableSQL)) {

			// esegue create table
			createTablePreparedStatement.executeUpdate();

		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
			result = -1;
		}

		return result;
	}

	
	/**
	 * chiude la connessione al DB
	 */
	public void closeConnection() {
		try {

			if (connection != null) {
				connection.close();
				connection = null;
			}

		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	/**
	 * crea l'insert statement
	 */
	public void prepareInsertStatement() {

		if (insertBatchPreparedStatement != null)
			closeInsertStatement();

		try {
			String insertTableSQL = "INSERT INTO " + TABLE_NAME + "(" + 
		COLUMN_DECIMAL_NAME + ", " + 
		COLUMN_INT_NAME + ", " + 
		COLUMN_VARCHAR_NAME + ", " + 
		COLUMN_DATE_NAME + 
		") VALUES" + "(?,?,?,?)";

			insertBatchPreparedStatement = connection.prepareStatement(insertTableSQL);
		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 *
	 * setta i parametri nel batch di insert
	 */
	public void setInsertDataBatch(List<Entry> entries) {

		if (entries == null) {
			throw new IllegalArgumentException("entries parameter cannot be null");
		}

		try {
			connection.setAutoCommit(false);	//disattivo autocommit

			//per pulizia
			insertBatchPreparedStatement.clearBatch();

			for (Entry entry : entries) {
				insertBatchPreparedStatement.setBigDecimal(1, entry.getDecimalField());
				insertBatchPreparedStatement.setInt(2, entry.getIntField());
				insertBatchPreparedStatement.setString(3, entry.getVarcharField());
				insertBatchPreparedStatement.setTimestamp(4, entry.getDateField());
				insertBatchPreparedStatement.addBatch();
			}
		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * esegue il batch di insert e poi fa la commit.
	 */
	public void execInsertDataBatch() throws SQLException {
		try {
			insertBatchPreparedStatement.executeBatch();
			connection.commit();
		} catch (SQLException ex) {
			connection.rollback();
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * chiude il prepared statement dell'insert
	 */
	public void closeInsertStatement() {
		try {
			insertBatchPreparedStatement.close();
			insertBatchPreparedStatement = null;
		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public int getMaxPrimaryKeyValue() {

		int maxPrimaryKeyValue = 0;

		try {
			String selectSQL = "SELECT MAX(" + COLUMN_PK_NAME + ") FROM " + TABLE_NAME;
			PreparedStatement selectMaxPKPreparedStatement = connection.prepareStatement(selectSQL);

			connection.setAutoCommit(true);
			ResultSet rs = selectMaxPKPreparedStatement.executeQuery();

			if (rs.next()) {
				maxPrimaryKeyValue = rs.getInt(1);
			}

			rs.close();
			selectMaxPKPreparedStatement.close();

		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}

		return maxPrimaryKeyValue;

	}

	/**
	 * crea il select statement
	 */
	public void prepareSelectStatement() {
		if (selectPreparedStatement != null)
			closeSelectStatement();

		try {
			String selectSQL = "SELECT " + COLUMN_PK_NAME + ", " + COLUMN_DECIMAL_NAME + ", " + COLUMN_INT_NAME + ", "
					+ COLUMN_VARCHAR_NAME + ", " + COLUMN_DATE_NAME + " FROM " + TABLE_NAME + " WHERE " + COLUMN_PK_NAME
					+ " = ?";

			selectPreparedStatement = connection.prepareStatement(selectSQL);
		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 *
	 * setta la PK nel select statement
	 */
	public void setSelectDataPK(int primaryKey) {
		try {
			connection.setAutoCommit(true);
			selectPreparedStatement.setInt(1, primaryKey);
		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * esegue il select statement
	 */
	public ResultSet execSelectData() {
		try {
			// esegue SELECT
			ResultSet rs = selectPreparedStatement.executeQuery();
			return rs;
		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	/**
	 *  chiude il select statement
	 */
	public void closeSelectStatement() {
		try {
			selectPreparedStatement.close();
			selectPreparedStatement = null;
		} catch (SQLException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
