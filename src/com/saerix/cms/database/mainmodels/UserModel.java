package com.saerix.cms.database.mainmodels;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.Model;
import com.saerix.cms.database.Row;
import com.saerix.cms.database.Table;


@Table(name = "users", rowclass = UserModel.UserRow.class)
public class UserModel extends Model {
	public static class UserRow extends Row {
		public int id() {
			return (Integer) getValue("user_id");
		}
		
		public String username() {
			return (String) getValue("username");
		}
	}
	
	public UserRow verify(String username, String password) throws DatabaseException {
		where("username", username);
		where("password", password);
		
		return (UserRow) get().getRow();
	}
	
	public UserRow getUser(int userId) throws DatabaseException {
		where("user_id", userId);
		return (UserRow) get().getRow();
	}
	
	public long register(String username, String password) throws DatabaseException {
		Map<String, Object> insert = new HashMap<String, Object>();
		insert.put("username", username);
		insert.put("password", hashPassword(password));
		
		return (Long) insert(insert);
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
