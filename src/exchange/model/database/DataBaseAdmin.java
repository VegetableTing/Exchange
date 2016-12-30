package exchange.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public  class DataBaseAdmin {
	
	private static  Connection connection = null; //�ΥH���Ʈw�s��������


	//�إ߳s�u
	//�ѼƬ���Ʈw�b���K�X
	public static void openConnection(String user,String pwd) {
		System.out.println("-------- MySQL JDBC Connection ------------");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found !!"); //�䤣��JDBC Driver  
			return;
		}
		System.out.println("MySQL JDBC Driver Registered!"); // ���\���U

		try {
			connection = DriverManager // jdbc:mysql://localhost:3306/data
					.getConnection("jdbc:mysql://localhost:3306/exchange?useUnicode=true&characterEncoding=UTF-8",user,pwd);
			//�n�J��Ʈw
			System.out.println("SQL Connection to database established!");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console"); //�P��Ʈw�s������
			System.out.println(e.getMessage());
			return;
		}

	}
	//�פ�s�u
	public static void closeConnection() {

		try {
			if (connection != null)
				connection.close();
			System.out.println("Connection closed !!");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	//�ΥH�USelect Query��method 
	//String query = "select  * from table_name";
	public static ResultSet selectDB(String query){
		
		ResultSet result = null;
		try{
		PreparedStatement statement = connection.prepareStatement(query);
		result = statement.executeQuery(); //�N SELECT���G�s���result 
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return result; 
	} 
	//�ΥHinsert, update, delete ��method 
	//UPDATE favorites SET type_name = 'swim' where account = 'admin'
	//INSERT INTO Customers " + "VALUES (1001, 'Simpson', 'Mr.', 'Springfield', 2001)
	//DELETE FROM Registration " +"WHERE id = 101";
	public  static void updateDB(String query){
		
		try{
		PreparedStatement statement = connection.prepareStatement(query);
		statement.executeUpdate(query);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return ;
	} 

	
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		
		// method �ϥνd�� �ȨѰѦ�
		ResultSet result = null; //�ΥH�ӱ�Select���G
		DataBaseAdmin.openConnection("root","root"); //�إ߳s�u, ��J��Ʈw�b���K�X
		String query = "UPDATE favorites SET type_name = 'swim' where account = 'admin'"; //QUERY
		DataBaseAdmin.updateDB(query); 
		
	}

}