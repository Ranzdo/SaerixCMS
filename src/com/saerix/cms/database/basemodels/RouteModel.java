package com.saerix.cms.database.basemodels;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;

@Table(name = "routes", rowclass = RouteModel.RouteRow.class)
public class RouteModel extends Model {
	public static enum RouteType {
		CONTROLLER(0),
		REDIRECT(1)
		;
		private int id;
		private RouteType(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		
		public static RouteType valueOfId(int id) {
			for(RouteType rt : values())
				if(rt.getId() == id)
					return rt;
			
			throw new IllegalArgumentException("There is no routetype with id "+id);
		}
	}
	
	public static class RouteRow extends Row {
		public RouteType getType() {
			return RouteType.valueOfId((Integer) getValue("route_type"));
		}
		
		public String getRouteValue() {
			return (String) getValue("route_value");
		}
	}
	
	public RouteRow getRoute(int hostId, String suffix) throws DatabaseException {
		where("route_suffix", suffix);
		where("host_id", hostId);
		return (RouteRow) get().getRow();
	}
}
