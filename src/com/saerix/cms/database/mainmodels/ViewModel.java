package com.saerix.cms.database.mainmodels;
import java.util.HashMap;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Result;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;


@Table(name = "views", rowclass = ViewModel.ViewRow.class)
public class ViewModel extends Model {
	public static class ViewRow extends Row {
		public int getId() {
			return (Integer) get("view_id");
		}
		
		public String getName() {
			return (String) get("view_name");
		}
		
		public String getContent() {
			return (String) get("view_content");
		}
	}
	
	public ViewRow getView(int hostId, String viewName) throws DatabaseException {
		where("host_id", hostId);
		where("view_name", viewName);
		return (ViewRow) get().getRow();
	}
	
	public Result getViews() throws DatabaseException {
		return get();
	}
	
	public Result getViews(int hostId) throws DatabaseException {
		where("host_id", hostId);
		return get();
	}
	
	public Object addView(int hostId, String viewName, String content) throws DatabaseException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("host_id", hostId);
		values.put("view_name", viewName);
		values.put("view_content", content);
		return insert(values);
	}
	
	public int removeView(int hostId, String viewName) throws DatabaseException {
		where("host_id", hostId);
		where("view_name", viewName);
		return remove();
	}
}
