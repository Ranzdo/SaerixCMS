package com.saerix.cms.database;

import java.util.Properties;

import com.saerix.cms.database.mainmodels.ControllerModel;
import com.saerix.cms.database.mainmodels.DatabaseModel;
import com.saerix.cms.database.mainmodels.HostModel;
import com.saerix.cms.database.mainmodels.ModelModel;
import com.saerix.cms.database.mainmodels.RouteModel;
import com.saerix.cms.database.mainmodels.UserModel;
import com.saerix.cms.database.mainmodels.ViewModel;


public class MainDatabase extends DatabaseDefinedDatabase {	

	private static Class<?>[] baseModels = {
		DatabaseModel.class,
		ModelModel.class,
		ViewModel.class,
		RouteModel.class,
		ControllerModel.class,
		UserModel.class,
		HostModel.class
	};
	
	@SuppressWarnings("unchecked")
	MainDatabase(DatabaseLoader databaseLoader, Properties properties) throws DatabaseException {
		super(databaseLoader, "main", properties);
		for(Class<?> model : baseModels) {
			if(!model.isAnnotationPresent(Table.class))
				throw new DatabaseException("TableConfig annonation missing for "+model.getName());
		
			Table config = model.getAnnotation(Table.class);
			
			registerModel(new LoadedModel(config.name(), (Class<? extends Model>) model, config.rowclass()));
		}
	}
}
