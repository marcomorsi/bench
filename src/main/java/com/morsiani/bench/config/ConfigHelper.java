package com.morsiani.bench.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * classe che fornisce il valore delle propriet� di configurazione dei tests e del DB
 */
public class ConfigHelper {

	private static final int DEFAULT_NUM_OF_BATCH_INSERT_EXECUTIONS = 100;
	private static final int DEFAULT_NUM_OF_INSERT_PER_TRANSACTION = 10;

	private static final int DEFAULT_NUM_OF_SELECT_EXECUTIONS = 100;

	private static final String CONFIG_FILE = "config.properties";

	private Properties props;

	public ConfigHelper() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		props = new Properties();
		try (InputStream resourceStream = loader.getResourceAsStream(CONFIG_FILE)) {
			props.load(resourceStream);
		} catch (IOException ex) {

			String error = "Failed to load config props.";
			Logger.getLogger(ConfigHelper.class.getName()).log(Level.SEVERE, error, ex);
		}

	}

	
	//metodi per accesso a propriet� dei tests

	/**
	 * @return n. esecuzioni batch-insert da file config. 
	 *         (se la props non e' settata o se <= 0, ritorna il default)
	 */
	public int getNumberOfBatchInsertExecutions() {
		if (props == null)
			return DEFAULT_NUM_OF_BATCH_INSERT_EXECUTIONS;

		int numOfExecutions;

		String numOfExecutionsProp = props.getProperty(ConfigProps.NUM_OF_BATCH_INSERT_EXECUTIONS);

		try {
			numOfExecutions = Integer.parseInt(numOfExecutionsProp);
		} catch (NumberFormatException numberFormatException) {
			numOfExecutions = 0;
		}

		if (numOfExecutions <= 0) {
			String warning = "Bad input for property " + ConfigProps.NUM_OF_BATCH_INSERT_EXECUTIONS + ".\n"
					+ "Input value is not a positive number: " + numOfExecutionsProp + ".\n" 
					+ "Using default value " + DEFAULT_NUM_OF_BATCH_INSERT_EXECUTIONS + "\n\n";

			Logger.getLogger(ConfigHelper.class.getName()).log(Level.WARNING, warning);

			numOfExecutions = DEFAULT_NUM_OF_BATCH_INSERT_EXECUTIONS;
		}

		return numOfExecutions;
	}

	/**
	 * @return n. inserts per transazione da file config. 
	 *         (se la props non e' settata o se <= 0, ritorna il default)
	 */
	public int getNumberOfInsertsPerTransaction() {
		if (props == null)
			return DEFAULT_NUM_OF_INSERT_PER_TRANSACTION;

		int numOfExecutions;

		String numOfExecutionsProp = props.getProperty(ConfigProps.NUM_OF_INSERTS_PER_TRANSACTION);

		try {
			numOfExecutions = Integer.parseInt(numOfExecutionsProp);
		} catch (NumberFormatException numberFormatException) {
			numOfExecutions = 0;
		}

		if (numOfExecutions <= 0) {
			String warning = "Bad input for property " + ConfigProps.NUM_OF_INSERTS_PER_TRANSACTION + ".\n"
					+ "Input value is not a positive number: " + numOfExecutionsProp + ".\n" 
					+ "Using default value " + DEFAULT_NUM_OF_INSERT_PER_TRANSACTION + "\n\n";

			Logger.getLogger(ConfigHelper.class.getName()).log(Level.WARNING, warning);

			numOfExecutions = DEFAULT_NUM_OF_INSERT_PER_TRANSACTION;
		}

		return numOfExecutions;
	}

	/**
	 * @return n. esecuzioni select da file config. 
	 *         (se la props non e' settata o se <= 0, ritorna il default)
	 */
	public int getNumberOfSelectExecutions() {
		if (props == null)
			return DEFAULT_NUM_OF_SELECT_EXECUTIONS;

		int numOfExecutions;

		String numOfExecutionsProp = props.getProperty(ConfigProps.NUM_OF_SELECT_EXECUTIONS);

		try {
			numOfExecutions = Integer.parseInt(numOfExecutionsProp);
		} catch (NumberFormatException numberFormatException) {
			numOfExecutions = 0;
		}

		if (numOfExecutions <= 0) {
			String warning = "Bad input for property " + ConfigProps.NUM_OF_SELECT_EXECUTIONS + ".\n"
					+ "Input value is not a positive number: " + numOfExecutionsProp + ".\n" 
					+ "Using default value " + DEFAULT_NUM_OF_SELECT_EXECUTIONS + "\n\n";

			Logger.getLogger(ConfigHelper.class.getName()).log(Level.WARNING, warning);

			numOfExecutions = DEFAULT_NUM_OF_SELECT_EXECUTIONS;
		}

		return numOfExecutions;
	}

	
	//metodi per accesso a propriet� del DB. Se non c'e' la proprita' fornisce una stringa vuota
	
	public String getServerName() {
		if (props == null)
			return null;

		return props.getProperty(ConfigProps.SERVER_NAME, "");

	}

	public String getServerPortNumber() {
		if (props == null)
			return null;

		return props.getProperty(ConfigProps.SERVER_PORT, "");

	}

	public String getDatabaseName() {
		if (props == null)
			return null;

		return props.getProperty(ConfigProps.DATABASE_NAME, "");

	}

	public String getUsername() {
		if (props == null)
			return null;

		return props.getProperty(ConfigProps.USERNAME, "");

	}

	public String getPassword() {
		if (props == null)
			return null;

		return props.getProperty(ConfigProps.PASSWORD, "");

	}

}
