package com.morsiani.bench.test;

import com.morsiani.bench.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tester per l'esecuzione di SELECT su DB. Calcola i tempi di esecuzione min,
 * max e avg della select di un record tramite PK.
 * 
 */
public class SelectTester extends Tester {

	private int maxPrimaryKeyValue;

	/**
	 * @param numOfExecutions
	 *            numero delle volte in cui verr� eseguita la SELECT
	 */
	public SelectTester(int numOfExecutions) {
		super(numOfExecutions);
	}

	@Override
	protected int init() {
		int result = super.init();

		if (result != 0)
			return result;
		// ricavo la PK piu grande per poi calcolare una key random per la select
		maxPrimaryKeyValue = dbHelper.getMaxPrimaryKeyValue();

		dbHelper.prepareSelectStatement();

		return result;
	}

	// in questa fase imposto i parametri per la select PreparedStatement
	@Override
	protected void beforeTest() {

		int primaryKeyValue = 0;

		// per ipotesi le PK in DB sono valori tra 1 e maxPrimaryKeyValue
		if (maxPrimaryKeyValue > 0)
			primaryKeyValue = Utils.getRandomInt(maxPrimaryKeyValue);

		dbHelper.setSelectDataPK(primaryKeyValue);
	}

	@Override
	protected void test(Holder<ResultSet> holder) {

		// calcoliamo il tempo per eseguire la SELECT
		ResultSet rs = dbHelper.execSelectData();
		holder.setContent(rs);
	}

	@Override
	protected void afterTest(Holder<ResultSet> holder) {

		try {
			ResultSet rs = holder.getContent();

			// close the result set
			if (rs != null)
				rs.close();

		} catch (SQLException ex) {
			Logger.getLogger(SelectTester.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	@Override
	protected void finish() {
		dbHelper.closeSelectStatement();
		super.finish();
	}

	@Override
	public String getTestName() {
		return "Select Statements by PK";
	}

}
