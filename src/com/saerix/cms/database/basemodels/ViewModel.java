package com.saerix.cms.database.basemodels;
import java.sql.SQLException;

import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.TableConfig;

@TableConfig(name = "views", rowclass = ViewModel.ViewRow.class)
public class ViewModel extends Model {
	public static class ViewRow extends Row {
		public int getId() {
			return (Integer) getValue("view_id");
		}
		
		public String getName() {
			return (String) getValue("view_name");
		}
		
		public String getContent() {
			return (String) getValue("view_content");
		}
	}
	
	public ViewRow getView(String viewName) throws SQLException {
		where("view_name", viewName);
		return (ViewRow) get().getRow();
	}
}
