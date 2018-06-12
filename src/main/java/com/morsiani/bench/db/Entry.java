package com.morsiani.bench.db;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * pojo che mappa una entry della tabella di test
 */
public class Entry {

	private int pk;
	private BigDecimal decimalField;
	private int intField;
	private String varcharField;
	private Timestamp dateField;
	
	public Entry() {
	}

	public int getPK() {
		return pk;
	}

	public void setPK(int pk) {
		this.pk = pk;
	}

	public BigDecimal getDecimalField() {
		return decimalField;
	}

	public void setDecimalField(BigDecimal decimalField) {
		this.decimalField = decimalField;
	}

	public int getIntField() {
		return intField;
	}

	public void setIntField(int intField) {
		this.intField = intField;
	}

	public String getVarcharField() {
		return varcharField;
	}

	public void setVarcharField(String varcharField) {
		this.varcharField = varcharField;
	}

	public Timestamp getDateField() {
		return dateField;
	}

	public void setDateField(Timestamp dateField) {
		this.dateField = dateField;
	}

}
