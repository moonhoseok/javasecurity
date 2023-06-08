package aes;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * useraccount 테이블의 email 값을 읽어서, usersecurity테이블에 암호화(AES)해서 저장하기
 * 1. usersecurity테이블에 email 컬럼의 크기를 1000으로 변경하기 (db에서) 
 * 2. key는 userid의 해쉬값(sha-256)의 앞16자리로 설정한다.
 */
public class CipherMain3 {
	public static void main(String[] args) throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection
				("jdbc:mariadb://localhost:3306/gdudb","gdu","1234");
		PreparedStatement pstmt = conn.prepareStatement
				("select userid, email from useraccount");
		ResultSet rs = pstmt.executeQuery();
		/*
		String cipher ="";
		while(rs.next()) {
			String id = rs.getString("userid");
			String email = rs.getString("email");
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String hashid = "";
			byte[] plain = id.getBytes();
			byte[] hash = md.digest(plain);
			for(byte b : hash) {
				hashid += String.format("%02X", b);
			}
			String key = hashid.substring(0,15);
			cipher = CipherUtil.encrypt(email,key);
			pstmt.close();
			pstmt = conn.prepareStatement
				("update usersecurity set email=? where userid=?");
			pstmt.setString(1, cipher);
			pstmt.setString(2, id);
			pstmt.executeUpdate();
		}
		*/
		while(rs.next()) {
			String userid = rs.getString("userid");
			String email = rs.getString("email");
			String key = CipherUtil.makehash(userid);
			String cipher = CipherUtil.encrypt(email,key);
			pstmt =conn.prepareStatement
					("update usersecurity set email=? where userid=?");
			pstmt.setString(1, cipher);
			pstmt.setString(2, userid);
			pstmt.executeUpdate();
		}
		pstmt.close();
		pstmt = conn.prepareStatement("select userid,email from usersecurity");
		rs = pstmt.executeQuery();
		while(rs.next()) {
			System.out.println("userid : "+rs.getString("userid"));
			System.out.println("email : "+rs.getString("email"));	
		}
		
	}
}
