package com.morsiani.bench.test;

import com.morsiani.bench.db.Entry;
import com.morsiani.bench.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tester per l'esecuzione di INSERT su DB.
 * Gli statement vengono committati al DB in blocchi (batch) fissi di dimensione numInsertsPerTransaction.
 * Calcola i tempi di esecuzione min, max e avg dei batch.
 * Fornisce i tempi di inseriento di un singolo record.
 * 
 */
public class InsertTester extends Tester {

    private final int numInsertsPerTransaction;
    
    /**
     *
     * @param numOfBatchInsertExecutions numero volte che la batch-insert viene eseguita
     * @param numInsertsPerTransaction numero di insert eseguite in un singola batch
     */
    public InsertTester(int numOfBatchInsertExecutions, int numInsertsPerTransaction) {
        super(numOfBatchInsertExecutions);
        this.numInsertsPerTransaction = numInsertsPerTransaction;
    }
    
    @Override
    protected int init()
    {
        int result = super.init();
        
        if(result != 0 )
            return result;
        
        //utilizzo lo stesso oggetto PreparedStatement per tutto il test
        dbHelper.prepareInsertStatement();
        
        return result;
    }
    

    //in questa fase imposto i parametri per la insert PreparedStatement
    @Override
    protected void beforeTest() {
        
    	//lista di entry che verranno inserite nel DB
        List<Entry> entries = new ArrayList<Entry>();
        
        //init della lista
        for(int i = 0; i < numInsertsPerTransaction; i++ )
        {
            Entry entry = new Entry();
            entry.setVarcharField(Utils.getRandomString(20));
            entry.setIntField(Utils.getRandomInt(100000));
            entry.setDecimalField(Utils.getRandomBigDecimal(100000, 2));
            entry.setDateField(Utils.getCurrentTimeStamp());
            
            entries.add(entry);
        }
        
        dbHelper.setInsertDataBatch(entries);
    }
    
    @Override
    protected void test(Holder<ResultSet> holder) {
        try {
            dbHelper.execInsertDataBatch();
        } catch (SQLException ex) {
            Logger.getLogger(InsertTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void afterTest(Holder<ResultSet> holder) {
    }
    
     @Override
    protected void finish()
    {
        dbHelper.closeInsertStatement();
        super.finish();
    }

    @Override
    public String getTestName() {
        return "INSERT STATEMENTS";
    }
    
    @Override
    public String getTestInfo() {
        
        String info = "Batch Insert Executions: " + this.getNumOfExecutions() + "\n" + 
                      "N. inserts in each batch: " + numInsertsPerTransaction + "\n";
        
        return info;
    }
    
    @Override
    public Statistics getStats() {
        Statistics stats = new Statistics();
        
        if(this.getNumOfExecutions() > 0)
        {
            stats.put("min (to insert a batch of records)", this.getMinTime());
            stats.put("max (to insert a batch of records)", this.getMaxTime());
            stats.put("avg (to insert a batch of records)", this.getAvgTime());

            long minTimePerRecord = 0;
            long maxTimePerRecord = 0;
            long avgTimePerRecord = 0;

            if(numInsertsPerTransaction > 0)
            {
                //costo di una singola insert in batch con min time
                minTimePerRecord = this.getMinTime()/numInsertsPerTransaction;
                //costo di una singola insert in batch con max time
                maxTimePerRecord = this.getMaxTime()/numInsertsPerTransaction;
                //costo medio per singola insert
                avgTimePerRecord = this.getAvgTime()/numInsertsPerTransaction;
            }

            stats.put("time per record in the batch with min time", minTimePerRecord);
            stats.put("time per record in the batch with max time", maxTimePerRecord);
            stats.put("avg (to insert a record)", avgTimePerRecord);
        }

        return stats;
    }
 
}
