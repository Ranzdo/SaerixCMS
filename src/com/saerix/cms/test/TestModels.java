package com.saerix.cms.test;

import java.sql.SQLException;
import java.util.HashMap;

import com.saerix.cms.database.Database;
import com.saerix.cms.database.ModelTable;
import com.saerix.cms.database.ModelTable.ModelRow;

public class TestModels {
	public static void main(String[] args) throws SQLException {
		Database.initiate("127.0.0.1", 3306, "root", "", "saerixcms", "cms_");
		Database.database.tables.put("models", new ModelTable());
		Database.getTable("models").database = Database.database;
		Database.getTable("models").setup();
		
		((ModelTable)Database.getTable("models")).getRow(2).update("model_name", "varför?!");
		
		System.out.print(((ModelTable)Database.getTable("models")).getRow(2));
		
	}
}
