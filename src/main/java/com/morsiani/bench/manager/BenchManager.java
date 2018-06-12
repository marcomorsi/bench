package com.morsiani.bench.manager;

import java.util.ArrayList;
import java.util.List;

import com.morsiani.bench.config.ConfigHelper;
import com.morsiani.bench.db.DBHelper;
import com.morsiani.bench.test.InsertTester;
import com.morsiani.bench.test.SelectTester;
import com.morsiani.bench.test.Tester;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esegue test di insert/select su DB. 
 * Gestisce una lista di Tester (che eseguono i test) e stampa i loro risultati. 
 * Inizializza il manager ( init() ) inserendo i Tester alla lista
 * e istanzia le risorse necessarie. Se necessario 
 * libera le risorse al completamento dei test ( finish() ).
 * 
 */
public class BenchManager {

	private final List<Tester> testers;

	public BenchManager() {
		this.testers = new ArrayList<Tester>();
	}

	public List<Tester> getTesters() {
		return testers;
	}

	/**
	 * metodo che esegue i tests:<br>
	 * - invoca init() per inizializzare i tests<br>
	 * - per ogni Tester che e' stato aggiunto al manager esegue i suoi tests e
	 * stampa i risultati.<br>
	 * - invoca finish() per liberare le risorse
	 */
	public void runTesters() {
		
		int initResult = init();

		if (initResult != 0) {
			String error = "BenchManager initialization failed.";
			Logger.getLogger(BenchManager.class.getName()).log(Level.SEVERE, error);

			return;
		}

		for (Tester t : testers) {
			printTesterStart(t);
			t.runTests();
			printTesterStatistics(t);
		}

		finish();
	}

	/**
	 * Inizializza le risorse necessarie per i test. 
	 * Se diverso da 0, e' fallita e non esegue i test
	 */
	protected int init() {

		// legge le props di configurazione per i test da svolgere
		ConfigHelper helper = new ConfigHelper();
		int numOfBatchInsertExecutions = helper.getNumberOfBatchInsertExecutions();
		int numOfInsertStatementsPerTransaction = helper.getNumberOfInsertsPerTransaction();
		int numOfSelectExecutions = helper.getNumberOfSelectExecutions();

		DBHelper dbHelper = new DBHelper();

		if (dbHelper.connect() != 0) {
			return -2;
		}

		// crea la tabella da usare nei test.
		int createTableResult = dbHelper.createTable();
		
		dbHelper.closeConnection();

		// se si verificano errrori alla creazione della tabella, non esegue i tests.
		if (createTableResult != 0)
			return -3;

		// aggiunge il tester per i test di insert
		InsertTester insertTester = new InsertTester(numOfBatchInsertExecutions, numOfInsertStatementsPerTransaction);
		testers.add(insertTester);

		// aggiunge il tester per i test di select
		SelectTester selectTester = new SelectTester(numOfSelectExecutions);
		testers.add(selectTester);

		return 0;
	}

	/**
	 * metodo per liberare le risorse usate durante i tests (se necessario).
	 */
	protected void finish() {
		// si puo' valutare di fare il drop della tabella. 
	}

	private void printTesterStart(Tester tester) {
		System.out.println("Running tester: " + tester.getTestName());
		System.out.println("-----------------------------------------------------");
	}

	private void printTesterStatistics(Tester tester) {
		System.out.println("Statitics");
		System.out.println(tester.getTestInfo());
		tester.getStats().print();
		System.out.println("######################################################");
		System.out.println("\n");

	}
}
