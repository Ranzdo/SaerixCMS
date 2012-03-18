package com.saerix.cms.database.basemodels;

import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.TableConfig;

@TableConfig(name = "controllers", rowclass = ControllerModel.ControllerRow.class)
public class ControllerModel extends Model {
	public static class ControllerRow extends Row {
		
	}
	
	
	
}
