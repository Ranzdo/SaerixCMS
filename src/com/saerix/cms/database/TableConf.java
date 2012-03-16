package com.saerix.cms.database;

import com.saerix.cms.database.Model.Row;

public class TableConf {
	private String tableName;
	private boolean persistent;
	private Class<? extends Row> rowClass;
	
	public TableConf(TableConfig tableConfig) {
		this.tableName = tableConfig.name();
		this.persistent = tableConfig.persistent();
		this.rowClass = tableConfig.rowclass();
	}

	public TableConf(String tableName, boolean persistent, Class<? extends Row> rowClass) {
		this.tableName = tableName;
		this.persistent = persistent;
		this.rowClass = rowClass;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	public Class<? extends Row> getRowClass() {
		return rowClass;
	}

	public void setRowClass(Class<? extends Row> rowClass) {
		this.rowClass = rowClass;
	}
}
