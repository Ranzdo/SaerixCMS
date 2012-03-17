package com.saerix.cms.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@TableConfig(name = "templates", rowclass = TemplateTable.TemplateRow.class)
public class TemplateTable extends Model {
	public static class TemplateRow extends Row {
		
	}
	
	public TemplateRow getTemplate(String templateName) {
		try {
			PreparedStatement p = prepareStatement("SELECT * FROM "+getTableName()+" WHERE template_name = ?");
			p.setString(1, templateName);
			ResultSet rs = p.executeQuery();
			if(rs.first())
				return (TemplateRow) new TemplateRow().set(rs);
			else
				return null;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
