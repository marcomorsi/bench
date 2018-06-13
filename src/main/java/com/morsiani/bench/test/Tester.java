package com.morsiani.bench.test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.morsiani.bench.db.DBHelper;

/**
 * Classe astratta per il test di performance. Fornisce un Result.
 * Esegue uno stesso test numOfExecutions volte e calcola i tempi di esecuzione 
 * min, max e medio (in nanosecondi).
 * 
 */
public abstract class Tester {

	private final int numOfExecutions;
	private List<Long> executionTimes;

	private long minTime;
	private long maxTime;
	private long avgTime;

	/**
	 * usato dalle sotto classi concrete per eseguire statement sul db
	 */
	protected DBHelper dbHelper;

	/**
	 *
	 * @param numOfExecutions indica il numero di volte che il metodo execTest(State state)
	 *            viene eseguito per raccogliere i risultati statistici.
	 */
	public Tester(int numOfExecutions) {
		initTimeProperties();
		this.numOfExecutions = numOfExecutions;
	}

	private void initTimeProperties() {
		this.avgTime = 0;
		this.maxTime = 0;
		this.minTime = Long.MAX_VALUE;
		this.executionTimes = new ArrayList<>();
	}

	public int getNumOfExecutions() {
		return numOfExecutions;
	}

	public long getMinTime() {
		return minTime;
	}

	public long getMaxTime() {
		return maxTime;
	}

	public long getAvgTime() {
		return avgTime;
	}

	
    /**
     * esegue lo stesso test numOfExecutions volte e calcola i tempi di esecuzione
     * le statistiche sono disponibili tramite getResult()
    * @return nome del test eseguito.
    */
	public abstract String getTestName();
	
	public void runTests() {

		// inizializza il test
		int initResult = init();

		if (initResult != 0) {
			String error = "Tester initialization failed.\n\n";
			Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, error);

			return;
		}

		// init dei tempi
		initTimeProperties();
		
		Holder<ResultSet> holder = new Holder<ResultSet>();
		//State state = makeState();

		long totalIterationsTime = 0;

		for (int i = 0; i < numOfExecutions; i++) {

			// da eseguire prima di ogni test
			preTest();

			// esegue il test e calcola il tempo di esecuzuione
			long startTime = System.nanoTime();
			test(holder);
			long end = System.nanoTime();
			long executionTime = end - startTime;

			// da eseguire dopo ogni test
			postTest(holder);

			// aggiunge l'esecuzione alla lista
			executionTimes.add(executionTime);

			// verifica se il tempo dell'esecuzione corrente e' un tempo min o max
			if (executionTime < minTime)
				minTime = executionTime;
			if (executionTime > maxTime)
				maxTime = executionTime;

			// aggiunge il tempo dell'esecuzione corrente al tempo delle iterazioni totali
			totalIterationsTime += executionTime;
		}

		// calcola il tempo di esecuzione medio
		if (numOfExecutions > 0)
			avgTime = totalIterationsTime / numOfExecutions;

		// libera le risorse
		finish();
	}

	/**
	 * inizializza le risorse per il test
	 * @return 0 se inizializzato con successo. Altrimenti un numero diverso da 0.
	 */
	protected int init() {
		dbHelper = new DBHelper();
		int connectionResult = dbHelper.connect();

		return connectionResult;
	}
	
	/**
	 * operazioni da eseguirsi prima di ogni test
	 */
	protected abstract void preTest();

	/**
	 * test vero e proprio
	 * Il tester misura i tempi di esecuzione del metodo e fornisce i risultati statistici.
	 */
	protected abstract void test(Holder<ResultSet> holder);

	/**
	 * operazioni da eseguirsi dopo ogni test
	 */
	protected abstract void postTest(Holder<ResultSet> holder);

	
	/**
	 * metodo per liberare le risorse usate durante i tests
	 */
	protected void finish() {
		dbHelper.closeConnection();
	}

    /**
     * Restituisce una mappa Statistics con le stitistiche di esecuzione dei test.
     * 
     * @return oggetto con i tempi registrati durante l'esecuzione.
     */
	public Statistics getStats() {
		Statistics stats = new Statistics();

		if (numOfExecutions > 0) {
			stats.put("tempo min", minTime);
			stats.put("tempo max", maxTime);
			stats.put("tempo avg", avgTime);
		}

		return stats;
	}

    /**
    *
    * @return info sul test eseguito.
    */
	public String getTestInfo() {

		return "N. esecuzioni: " + numOfExecutions + "\n";
	}

}
