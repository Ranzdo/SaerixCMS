package com.saerix.cms.database.basemodels;

import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;

@Table(name = "databases", rowclass = DatabaseModel.DatabaseRow.class)
public class DatabaseModel {
	public static class DatabaseRow extends Row {
		
	}
	
}
