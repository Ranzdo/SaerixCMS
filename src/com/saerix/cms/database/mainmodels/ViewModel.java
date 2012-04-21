package com.saerix.cms.database.mainmodels;

import java.util.Map;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Result;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;


@Table(name = "views", rowclass = ViewModel.ViewRow.class)
public class ViewModel extends Model {
	public static class ViewRow extends Row {
		public int getHostId() {
			return (Integer) get("host_id");
		}
		
		public String getName() {
			return (String) get("view_name");
		}
		
		public String getContent() {
			return (String) get("view_content");
		}
	}
	
	public Result getView(int hostId, String viewName) throws DatabaseException {
		where("host_id", hostId);
		where("view_name", viewName);
		return get();
	}
	
	public Result getViews() throws DatabaseException {
		return get();
	}
	
	public Result getViews(int hostId) throws DatabaseException {
		where("host_id", hostId);
		return get();
	}
	
	public void addView(int hostId, String viewName, Map<String, Object> values) throws DatabaseException {
		values.put("host_id", hostId);
		values.put("view_name", viewName);
		insert(values);
	}
	
	public void removeView(int hostId, String viewName) throws DatabaseException {
		where("host_id", hostId);
		where("view_name", viewName);
		remove();
	}
	
	public void updateView(int hostId, String viewName, Map<String, Object> values) throws DatabaseException {
		where("host_id", hostId);
		where("view_name", viewName);
		update(values);
	}
}
