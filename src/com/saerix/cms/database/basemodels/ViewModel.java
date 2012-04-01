package com.saerix.cms.database.basemodels;
import java.sql.SQLException;
import java.util.List;

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
	
	public ViewRow getView(int hostId, String viewName) throws SQLException {
		where("host_id", hostId);
		where("view_name", viewName);
		return (ViewRow) get().getRow();
	}
	
	@SuppressWarnings("unchecked")
	public List<ViewRow> getAllViews() throws SQLException {
		return (List<ViewRow>) get().getRows();
	}
}
