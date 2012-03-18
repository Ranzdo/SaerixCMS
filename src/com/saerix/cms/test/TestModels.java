package com.saerix.cms.test;

import java.sql.SQLException;

import com.saerix.cms.SaerixCMS;
import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.ViewModel;

public class TestModels {
	public static void main(String[] args) throws SQLException {
		SaerixCMS.getProperties().put("mysql_hostname", "127.0.0.1");
		SaerixCMS.getProperties().put("mysql_port", "3306");
		SaerixCMS.getProperties().put("mysql_username", "root");
		SaerixCMS.getProperties().put("mysql_password", "");
		SaerixCMS.getProperties().put("mysql_database", "saerixcms");
		SaerixCMS.getProperties().put("mysql_prefix", "cms_");
		
		System.out.print(((ViewModel)Database.getTable("templates")).getTemplate("hahaha").toString());
		
	}
}
