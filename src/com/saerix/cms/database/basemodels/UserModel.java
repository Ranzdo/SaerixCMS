package com.saerix.cms.database.basemodels;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.TableConfig;

@TableConfig(name = "users", rowclass = UserModel.UserRow.class)
public class UserModel extends Model {
	public static class UserRow extends Row {
		public int id() {
			return (Integer) getValue("user_id");
		}
		
		public String username() {
			return (String) getValue("username");
		}
	}
	
	public UserRow verify(String username, String password) throws SQLException {
		where("username", username);
		where("password", hashPassword(password));
		
		return (UserRow) get().getRow();
	}
	
	public UserRow getUser(int userId) throws SQLException {
		where("user_id", userId);
		return (UserRow) get().getRow();
	}
	
	public int register(String username, String password) throws SQLException {
		Map<String, Object> insert = new HashMap<String, Object>();
		insert.put("username", username);
		insert.put("password", hashPassword(password));
		
		return (Integer) insert(insert);
	}
	
	private String hashPassword(String password) {
		MessageDigest hasher = null;
		try {
			hasher = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new String(hasher.digest(password.getBytes()));
	}
}
