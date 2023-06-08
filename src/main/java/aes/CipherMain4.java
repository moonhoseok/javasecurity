package aes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/*
 * usersecurity 테이블의 내용을 출력하기 
 * 이메일은 복호화 하여 출력하기 
 * 1. 암호화했던 key와 동일하게 처리
 */
public class CipherMain4 {
	public static void main(String[] args) throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection
				("jdbc:mariadb://localhost:3306/gdudb","gdu","1234");
		PreparedStatement pstmt = conn.prepareStatement
				("select userid, email from usersecurity");
		ResultSet rs = pstmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		for(int i =1;i<=rsmd.getColumnCount(); i++) {
			System.out.print(rsmd.getColumnName(i)+"\t");
		}
		System.out.println();
		while(rs.next()) {
			String userid = rs.getString("userid");
			String email = rs.getString("email"); // 암호화된 내용
			String key = CipherUtil.makehash(userid);
			String cipher = CipherUtil.decrypt(email,key); // 복호화된 내용
			System.out.println(userid+ " : "+cipher);
		}
	}
}
