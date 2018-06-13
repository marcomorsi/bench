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
    protected void preTest() {
        
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
    protected void postTest(Holder<ResultSet> holder) {
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
        
        String info = "N. esecuzioni di insert-batch: " + this.getNumOfExecutions() + "\n" + 
                      "N. insert in ogni batch: " + numInsertsPerTransaction + "\n";
        
        return info;
    }
    
    @Override
    public Statistics getStats() {
        Statistics stats = new Statistics();
        
        if(this.getNumOfExecutions() > 0)
        {
            stats.put("tempo min (di insert di un batch di record)", this.getMinTime());
            stats.put("tempo max (di insert di un batch di record)", this.getMaxTime());
            stats.put("tempo avg (di insert di un batch di record)", this.getAvgTime());

            long minTimePerRec = 0;
            long maxTimePerRec = 0;
            long avgTimePerRec = 0;

            if(numInsertsPerTransaction > 0)
            {
                //costo di una singola insert in batch con min time
                minTimePerRec = this.getMinTime()/numInsertsPerTransaction;
                //costo di una singola insert in batch con max time
                maxTimePerRec = this.getMaxTime()/numInsertsPerTransaction;
                //costo medio per singola insert
                avgTimePerRec = this.getAvgTime()/numInsertsPerTransaction;
            }

            stats.put("tempo di insert di un record nel batch con tempo min", minTimePerRec);
            stats.put("tempo di insert di un record nel batch con tempo max", maxTimePerRec);
            stats.put("tempo di insert di un record nel batch con tempo avg", avgTimePerRec);
        }

        return stats;
    }
 
}
