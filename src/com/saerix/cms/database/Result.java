package com.saerix.cms.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Result {
	private ArrayList<Row> result = new ArrayList<Row>();
	
	public int length = 0;
	
	Result(ResultSet rs, Class<? extends Row> rowclass) throws SQLException {
		while(rs.next()) {
			length++;
			try {
				result.add(rowclass.newInstance().set(rs));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Row getRow() {
		if(result.size() > 0)
			return result.get(0);
		else
			return null;
	}
	
	public List<? extends Row> getRows() {
		return result;
	}
}
