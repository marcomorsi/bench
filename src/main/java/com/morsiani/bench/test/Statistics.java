package com.morsiani.bench.test;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
/**
 * le chiavi indicano il tipo di statistica, i valori corrispondenti il tempo in nanosecondi. Es:
 *  - min 23023<br>
 *  - max 67322<br>
 *  - avg 38099
 * 
 */

//LinkedHashMap così mantengo l'ordine di inseriemnto delle chiavi
public class Statistics extends LinkedHashMap<String,Long>{
    
	private static final long serialVersionUID = 1L;

	/**
     * stampa descrizioni e tempi
     */
    public void print()
    {
        for (Entry<String,Long> e : entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue() + " nsec");
        }
    }
    
}
