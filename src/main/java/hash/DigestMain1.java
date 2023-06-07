package hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Scanner;
import java.util.Set;

/*
 * 해쉬 알고리즘 예제
 */
public class DigestMain1 {
	public static void main(String[] args) throws NoSuchAlgorithmException {
		byte[] plain = null;
		byte[] hash = null;
		// 자바에서 처리 가능한 해쉬 알고르즘 목록
		Set<String> algorithms = Security.getAlgorithms("MessageDigest");
		System.out.println(algorithms);
		String[] algo = {"MD5","SHA-1","SHA-256","SHA-512"};
		System.out.println("해쉬값을 구할 문자열을 입력하세요");
		Scanner scan = new Scanner(System.in);
		String str = scan.nextLine();
		plain = str.getBytes();
		for(String al : algo) {
			MessageDigest md = MessageDigest.getInstance(al); // 해쉬알고리즘 객체
			hash = md.digest(plain); // 해쉬값 
			//512비트 / 64바이티
			System.out.println(al+" 해쉬값 크기 : " +(hash.length*8) + "bits"); 
			System.out.println("해쉬값 : ");
			for(byte b : hash) {
				System.out.printf("%02X",b); // 16진수 2자리 코드값으로 출력
			}
			System.out.println();
		}
	}
}
