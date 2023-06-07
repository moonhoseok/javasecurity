package hash;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/*
 * 화면에서 아이디, 비밀번호를 입력받아서 usersecurity 테이블에서
 * 아이디가없으면 아이디 없음 메세지 출력
 * 비밀번호가 틀리면 비밀번호 오류 메세지 출력
 * 일치하는경우 : 반갑습니다. 이름님 출력하기
 * 1. 아이디, 비밀번호 입력받기
 * 2. 아이디에 맞는 정보를 usersecurity에서 읽기
 * 		해당 레코드가 없는경우 : 아이디없음 메세지출력
 * 3. 비밀번호 검증
 * 	- 입력된 비밀번호를 SHA-256알고리즘으로 해쉬값 구하기
 *  - 입력해쉬값 == db 해쉬값 비교하기
 *  - 일치	: 반갑습니다. 홍길동님 메세지 출력
 *  - 불일치	: 비밀번호 오류 메세지 출력
 */
public class DigestMain3 {
	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		System.out.println("아이디를 입력하세요");
		String id = scan.nextLine();
		System.out.println("비밀번호를 입력하세요");
		String pass = scan.nextLine();
		
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection
				("jdbc:mariadb://localhost:3306/gdudb","gdu","1234");
		PreparedStatement pstmt = conn.prepareStatement
				("select password,username from usersecurity where userid=?");
		pstmt.setString(1, id);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) { //아이디 존재
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			String hashpass = ""; // db에 등록된 형태로 입력비밀번호를 저장 변수
			byte[] plain = pass.getBytes();
			byte[] hash = md.digest(plain);
			for(byte b : hash) {
				hashpass += String.format("%02X", b);
			}
			if(rs.getString("password").equals(hashpass)) {
				System.out.println("반갑습니다. "+rs.getString("username")+"님");
			}else {
				System.out.println("비밀번호 오류");
			}
		}else { // 아이디 없음
			System.out.println("일치하는 아이디없음");
		}
	}
}
