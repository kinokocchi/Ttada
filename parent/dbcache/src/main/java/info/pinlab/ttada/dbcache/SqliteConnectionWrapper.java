package info.pinlab.ttada.dbcache;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteConnectionWrapper {
	private final String url;
	
	Connection conn = null;
	Statement statement = null;
	
	private String tableName = "pinjson"; 
	
	
	
	private SqliteConnectionWrapper(String url) throws SQLException{
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("DB can't be loaded!");
		}
		this.url = url;
		conn = DriverManager.getConnection(this.url);
		this.statement = conn.createStatement();
				
		if(isTableExists()){
			//-- great, do nothing --//
		}else{
			this.statement.executeUpdate("create table " + tableName +" (id integer, json string)");
		}
	}
	
	
	private boolean isTableExists() throws SQLException{
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet tables = meta.getTables(null, null, null, new String[] {"TABLE"});
		while(tables.next()){
			if(tableName.equals(tables.getString("TABLE_NAME"))){
				return true;
			}
		}
		return false;
	}

	
	
	public String readJsonString(String id){
		
		return null;
	}
	/**
	 * Updates or creates json data with given id. 
	 * 
	 * @param id for json string
	 * @param json the json string (escaped)
	 * @return the id
	 */
	public String updateJsonString(String id, String json){
		return null;
	}
	
	
	
	
	public boolean close(){
		try{
			if(!conn.isClosed()){
				conn.close();
				return true;
			}
		}catch(SQLException log){
			//-- ignore --//
		}
		return false;
	}
	
	
	public static class SqliteConnectionBuilder{
		String name = null;
		String path = null; //-- DB save path - if exists

		public SqliteConnectionBuilder setDbName(String name){
			this.name = name;
			return this;
		}
		public SqliteConnectionBuilder setDbPath(String path){
			this.path = path;
			return this;
		}
		
		
		public SqliteConnectionWrapper build(){
			if (name == null){
				throw new IllegalStateException("DB name can't be null!");
			}
			SqliteConnectionWrapper conn = null;
			try{
				conn = new SqliteConnectionWrapper("jdbc:sqlite:sample.db" + name);
			}catch(SQLException e){
				throw new IllegalStateException("DB error: '" + e.getMessage() +  "'");
			}
			return conn;
		}
	}
	
}
