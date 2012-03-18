package com.saerix.cms.database.basemodels;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.TableConfig;

@TableConfig(name = "views", rowclass = ViewModel.ViewRow.class)
public class ViewModel extends Model {
	public static class ViewRow extends Row {
		public ViewRow(ResultSet rs) throws SQLException {
			super(rs);
		}
	}
	
	public ViewRow getTemplate(String templateName) throws SQLException {
		where("view_name", templateName);
		return (ViewRow) get().getRow();
	}
}
