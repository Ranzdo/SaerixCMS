package com.saerix.cms.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The model
 * 
 * @author Ranzdo
 *
 */
public class Model {
	Database database;
	LoadedModel loaded;
	
	/**
	 * @return The table name with prefix
	 */
	public String getTableName() {
		return database.getTablePrefix()+loaded.getTableName();
	}
	
	/**
	 * Gets a prepared statement from the connection with the query
	 * 
	 * @param query SQL query to be executed
	 * @return A prepared statement with the supplied 
	 * @throws SQLException
	 */
	public PreparedStatement prepareStatement(String query) throws SQLException {
		Map<String, PreparedStatement> psmts = database.preparedStatementCache.get(Thread.currentThread());
		
		if(psmts == null) {
			psmts = new HashMap<String, PreparedStatement>();
			database.preparedStatementCache.put(Thread.currentThread(), psmts);
		}
		
		PreparedStatement psmt = psmts.get(query);
		
		if(psmt == null) {
			psmt = database.getConnection().prepareStatement(query);
			psmts.put(query, psmt);
		}
		
		return psmt;
	}
	
	/**
	 * @return The connection to the database
	 * @throws SQLException 
	 */
	public Connection getConnection() throws SQLException {
		return database.getConnection();
	}
	
	/**
	 * @return The class to make row objects of.
	 */
	protected Class<? extends Row> getRowClass() {
		return loaded.getRowClass();
	}
	
	/**
	 * Gets a model of a table in the database
	 * 
	 * @param tableName The name of the table
	 * @return An model of the table.
	 * @throws DatabaseException If an database exception occurs.
	 * @throws ModelNotFound If the model was not found.
	 */
	public Model model(String tableName) throws DatabaseException, ModelNotFound {
		return database.getModel(tableName);
	}
	
	//These setup the methods below, 
	
	//Where arguments
	private ArrayList<WhereAgrument> where = new ArrayList<WhereAgrument>();
	private String where_mod = "AND";
	
	public void where(String column, Object value) {
		where.add(new WhereAgrument(column, value));
	}
	
	public void where_mod(String where_mod) {
		this.where_mod = where_mod;
	}
	
	private String whereClause() {
		if(where.size() == 0)
			return "";
		
		StringBuilder whereClause = new StringBuilder(" WHERE ");
		for(int i = 0; i < where.size(); i++) {
			WhereAgrument arg = where.get(i);
			whereClause.append(arg.getColumn()+" "+arg.getComparer()+" ?");
			if(i != where.size()-1)
				whereClause.append(" "+where_mod+" ");
		}
		
		return whereClause.toString();
	}
	
	//Limit rows
	private int limit = -1;
	public void limit(int limit) {
		this.limit = limit;
	}
	
	private String limitClause() {
		if(limit > 0)
			return " LIMIT "+limit;
		else
			return "";
	}
	
	//Order settings
	private String orderByColumn = null;
	private String order = null;
	public void orderby(String column, String order) {
		this.orderByColumn = column;
		this.order = order;
	}
	
	private String orderClause() {
		if(orderByColumn != null && order != null)
			return "ORDER BY "+orderByColumn+" "+order;
		else
			return "";
	}
	
	
	private void clearClauses() {
		where.clear();
		where_mod = "AND";
		limit = -1;
		orderByColumn = null;
		order = null;
	}
	
	
	//These executes and depends on the calls above
	
	/**
	 * Update a table with 
	 * 
	 * @param values
	 * @return
	 * @throws DatabaseException
	 */
	public int update(Map<String, Object> values) throws DatabaseException {
		try {
			ArrayList<Object> ovalues = new ArrayList<Object>();
			StringBuilder sql = new StringBuilder("UPDATE "+getTableName()+" SET ");
			int counter = 1;
			for(Entry<String, Object> value : values.entrySet()) {
				sql.append(value.getKey()+" = ?");
				if(values.size() != counter)
					sql.append(",");
				ovalues.add(value.getValue());
				counter++;
			}
			
			sql.append(whereClause());
			sql.append(limitClause());
			
			PreparedStatement ps = prepareStatement(sql.toString());
			
			counter = 1;
			while(counter <= ovalues.size()) {
				ps.setObject(counter, ovalues.get(counter-1));
				counter++;
			}
			
			while(counter <= ovalues.size()+where.size()) {
				ps.setObject(counter, where.get(counter-ovalues.size()-1).getValue());
				counter++;
			}
			
			int result = ps.executeUpdate();
			
			clearClauses();
			return result;
		}
		catch(SQLException e) {
			throw (DatabaseException) new DatabaseException().initCause(e);
		}
	}

	public int remove() throws DatabaseException {
		try {
			if(where.size() == 0)
				throw new SQLException("No where arguement when remove() was called, please use trunacte() instead");
			
			PreparedStatement ps = prepareStatement("DELETE FROM "+getTableName()+whereClause()+limitClause());
			
			for(int i = 1; i <= where.size();i++)
				ps.setObject(i, where.get(i-1).getValue());

			int result = ps.executeUpdate();
			
			clearClauses();
			return result;
		}
		catch(SQLException e) {
			throw (DatabaseException) new DatabaseException().initCause(e);
		}
	}
	
	public Result get() throws DatabaseException {
		try {
			PreparedStatement ps = prepareStatement("SELECT * FROM "+getTableName()+whereClause()+limitClause()+orderClause());
			
			for(int i = 1; i <= where.size();i++)
				ps.setObject(i, where.get(i-1).getValue());
			
			ResultSet rs = ps.executeQuery();
			
			Result result = new Result(this, rs, getRowClass());
			
			rs.close();
			
			clearClauses();
			return result;
		}
		catch(SQLException e) {
			throw (DatabaseException) new DatabaseException().initCause(e);
		}
	}
	
	
	//Below does not listen to the method above 
	
	public int trunacte() throws DatabaseException {
		try {
			PreparedStatement ps = prepareStatement("TRUNCATE TABLE "+getTableName());
			int result = ps.executeUpdate();
			ps.close();
			return result;
		}
		catch(SQLException e) {
			throw (DatabaseException) new DatabaseException().initCause(e);
		}
	}
	
	public Object insert(Map<String, Object> values) throws DatabaseException {
		try {
			StringBuilder query = new StringBuilder("INSERT INTO "+getTableName()+" (");
			int counter = 0;
			for(Entry<String, Object> entry : values.entrySet()) {
				query.append(entry.getKey());
				counter++;
				if(counter != values.size()) {
					query.append(",");
				}
			}
			query.append(") VALUES (");
			for(int i = 0; i < values.size();i++) {
				query.append("?");
				if(i != values.size()-1) {
					query.append(",");
				}
			}
			query.append(")");
			PreparedStatement ps = database.getConnection().prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
			counter = 1;
			for(Entry<String, Object> entry : values.entrySet()) {
				ps.setObject(counter, entry.getValue());
				counter++;
			}
			ps.executeUpdate();
	
			ResultSet rs = ps.getGeneratedKeys();
			
			Object treturn = null;
			
			if(rs.first())
				treturn = rs.getObject(1);
			
			return treturn;
		}
		catch(SQLException e) {
			throw (DatabaseException) new DatabaseException().initCause(e);
		}
	}
}
