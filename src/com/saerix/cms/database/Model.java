package com.saerix.cms.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Model {
	Database database;
	private String primaryKeyColumn;
	private TableConfig tableConfig;
	
	public Model() {
		tableConfig = getClass().getAnnotation(TableConfig.class);
		if(tableConfig == null)
			throw new TableConfigNotSet(getClass());
	}
	
	void setup() {
		try {
			ResultSet keys = database.getConnection().getMetaData().getPrimaryKeys(null, null, getTableName());
			if(keys.first())
				primaryKeyColumn = keys.getString("COLUMN_NAME");
			else
				throw new TableHasNoPrimaryKeys(getTableName());
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return The table name with prefix
	 */
	public String getTableName() {
		return Database.mysql_prefix+tableConfig.name();
	}
	
	/**
	 * @return Gets the name of the first primary key column
	 */
	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	
	/**
	 * Gets a prepared statement from the connection with the query
	 * 
	 * @param query SQL query to be executed
	 * @return A prepared statement with the supplied 
	 * @throws SQLException
	 */
	public PreparedStatement prepareStatement(String query) throws SQLException {
		PreparedStatement ps = database.getConnection().prepareStatement(query);
		return ps;
	}
	
	/**
	 * @return The connection to the database
	 */
	public Connection getConnection() {
		return database.getConnection();
	}
	
	/**
	 * @return The set class to make row objects of.
	 */
	protected Class<? extends Row> getRowClass() {
		return tableConfig.rowclass();
	}
	
	
	//These setup the methods below, 
	
	//Where agruments
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
				whereClause.append(where_mod);
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
	
	//Order settnings
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
	
	
	//These executes and despends on the calls above
	public int update(Map<String, Object> values) throws SQLException {
		ArrayList<Object> ovalues = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("UPDATE "+getTableName()+" SET ");
		int counter = 1;
		for(Entry<String, Object> value : values.entrySet()) {
			sql.append(value.getKey()+" = ?");
			if(values.size() != counter)
				sql.append(",");
			ovalues.add(value.getValue());
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
		ps.close();
		
		clearClauses();
		return result;
	}

	public int remove() throws SQLException {
		if(where.size() == 0)
			throw new IllegalAccessError("Now where arguement when remove() was called, please use trunacte() instead");
		
		PreparedStatement ps = prepareStatement("DELETE FROM "+getTableName()+whereClause()+limitClause());
		
		for(int i = 1; i <= where.size();i++)
			ps.setObject(i, where.get(i));
		
		int result = ps.executeUpdate();
		ps.close();
		
		clearClauses();
		return result;
	}
	
	public Result get() throws SQLException {
		PreparedStatement ps = prepareStatement("SELECT * FROM "+getTableName()+whereClause()+limitClause()+orderClause());
		
		for(int i = 1; i <= where.size();i++)
			ps.setObject(i, where.get(i-1).getValue());
		
		ResultSet rs = ps.executeQuery();
		
		Result result = new Result(rs, getRowClass());
		
		rs.close();
		ps.close();
		
		clearClauses();
		return result;
	}
	
	
	//Below does not listen to the method above 
	
	public int remove(Object primaryKey) throws SQLException {
		PreparedStatement ps = prepareStatement("DELETE FROM "+getTableName()+" WHERE "+primaryKeyColumn+" = ?");
		ps.setObject(1, primaryKey);
		int result = ps.executeUpdate();
		ps.close();
		return result;
	}
	
	public int trunacte() throws SQLException {
		PreparedStatement ps = prepareStatement("TRUNCATE TABLE "+getTableName());
		int result = ps.executeUpdate();
		ps.close();
		return result;
	}
	
	public Object insert(Map<String, Object> values) throws SQLException {
		String query = "INSERT INTO "+getTableName()+" (";
		int counter = 0;
		for(Entry<String, Object> entry : values.entrySet()) {
			query = query.concat(entry.getKey());
			counter++;
			if(counter != values.size()) {
				query = query.concat(",");
			}
		}
		query = query.concat(") VALUES (");
		for(int i = 0; i < values.size();i++) {
			query = query.concat("?");
			if(i != values.size()-1) {
				query = query.concat(",");
			}
		}
		query = query.concat(")");
		PreparedStatement ps = database.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
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
		
		rs.close();
		ps.close();
		
		return treturn;
	}
	
	public Row getRow(Object primaryKey) throws SQLException {
		PreparedStatement ps = prepareStatement("SELECT * FROM "+getTableName()+" WHERE "+primaryKeyColumn+" = ?");
		ps.setObject(1, primaryKey);
		ResultSet rs = ps.executeQuery();
		if(rs.first()) {
			try {
				return getRowClass().newInstance().set(rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				rs.close();
			}
		}
		
		return null;
	}
}
