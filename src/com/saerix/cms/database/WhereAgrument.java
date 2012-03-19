package com.saerix.cms.database;

public class WhereAgrument {
	private final String column;
	private final String comparer;
	private final Object value;
	
	public WhereAgrument(String column, Object value) {
		String[] split = column.split(" ");
		if(split.length == 1) {
			this.column = column;
			this.comparer = "=";
		}
		else if(split.length > 2) {
			this.column = split[0];
			StringBuilder sb = new StringBuilder();
			for(int i = 1; i < split.length; i++)
				sb.append(split[i]);
			this.comparer = sb.toString();
		}
		else
			throw new IllegalArgumentException("The column name \""+column+"\" is not valid");
		
		this.value = value;
	}

	public String getColumn() {
		return column;
	}
	
	public String getComparer() {
		return comparer;
	}

	public Object getValue() {
		return value;
	}
}
