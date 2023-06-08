package aes;
//파일을 암호화 하기
public class CipherMain5 {
	public static void main(String[] args) {
		String key = "abc1234567";
		// p1.txt : 평문 텍스트 파일
		// c.sec : 암호문 파일 이름
		CipherUtil.encryptFile("p1.txt","c.sec",key);
	}

}
