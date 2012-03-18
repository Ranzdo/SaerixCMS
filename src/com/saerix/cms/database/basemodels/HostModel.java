package com.saerix.cms.database.basemodels;

import java.sql.SQLException;

import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.TableConfig;

@TableConfig(name = "hosts")
public class HostModel extends Model {
	public Row getHost(String hostValue) throws SQLException {
		where("host_value", hostValue);
		return get().getRow();
	}
}
