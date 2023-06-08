package aes;
/*
 * 키를 설정하여 AES 암호화 하기
 */
public class CipherMain2 {
	public static void main(String[] args) {
		String plain = "안녕하세요 홍길동 입니다."; 
		String key = "abc1234567";
		String cipher1 = CipherUtil.encrypt(plain,key);// 암호문
		System.out.println("암호문 : "+cipher1);// 암호문 출력. 실행시마다 다른 암호문 출력.key가 계속 변경됨
		String plain2 = CipherUtil.decrypt(cipher1,key);// 복호문
		System.out.println("복호문 : "+plain2);// plain1 == plain2 
	}
}
