package com.hepta.treino.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	
	private String url = "jdbc:mysql://localhost:3306/treino-hepta";
	private String user = "hepta";
	private String pass = "hepta";

	public Connection getConnection()
	{
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(url,user,pass);
		} catch(SQLException | ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

}
