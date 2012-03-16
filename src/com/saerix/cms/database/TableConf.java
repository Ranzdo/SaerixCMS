package com.saerix.cms.database;

import com.saerix.cms.database.Model.Row;

public class TableConf {
	private String tableName;
	private Class<? extends Row> rowClass;
	
	public TableConf(TableConfig tableConfig) {
		this.tableName = tableConfig.name();
		this.rowClass = tableConfig.rowclass();
	}

	public TableConf(String tableName, Class<? extends Row> rowClass) {
		this.tableName = tableName;
		this.rowClass = rowClass;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Class<? extends Row> getRowClass() {
		return rowClass;
	}

	public void setRowClass(Class<? extends Row> rowClass) {
		this.rowClass = rowClass;
	}
}
