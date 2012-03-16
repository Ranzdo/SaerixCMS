package com.saerix.cms.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@TableConfig(name = "templates")
public class TemplateTable extends Model {
	public class TemplateRow extends Row {
		public TemplateRow(ResultSet set) throws SQLException {
			super(set);
		}
	}
	
	public TemplateRow getTemplate(String templateName) throws SQLException {
		PreparedStatement p = prepareStatement("SELECT * FROM "+getTableName()+" WHERE template_name = ?");
		p.setString(1, templateName);
		ResultSet rs = p.executeQuery();
		if(rs.first())
			return new TemplateRow(rs);
		else
			return null;
	}
}
