package info.pinlab.ttada.dbcache;

import info.pinlab.ttada.dbcache.SqliteConnectionWrapper.SqliteConnectionBuilder;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;

public class SqliteTest {


	
	

	public static void main(String [] args) throws Exception{
		//-- load the class 
		Class.forName("org.sqlite.JDBC");

		
		
		
		SqliteConnectionWrapper conn = new SqliteConnectionBuilder().setDbName("test").build();
		conn.updateJsonString("dafs", "{}");
		conn.close();
		
		
		Connection connection = null;
		SQLiteConfig config = new SQLiteConfig();
		
		//DriverManager.getConnection("jdbc:sqlite:C:/work/mydatabase.db");
		connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		Statement statement = connection.createStatement();
		DatabaseMetaData meta = connection.getMetaData();
		

		
		
		System.out.println(meta.getDriverName());
		
		statement.setQueryTimeout(30);  // set timeout to 30 sec.

		statement.executeUpdate("drop table if exists person");
		statement.executeUpdate("create table person (id integer, name string)");
		statement.executeUpdate("insert into person values(1, 'leo')");
		statement.executeUpdate("insert into person values(2, 'yui')");
		ResultSet rs = statement.executeQuery("select * from person");
		while(rs.next())
		{
			// read the result set
			System.out.println("name = " + rs.getString("name"));
			System.out.println("id = " + rs.getInt("id"));
		}

		connection.close();
	}

}
