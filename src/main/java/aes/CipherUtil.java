package aes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil {
	private static byte[] randomKey;
	// 초기화벡터 : 첫번째 블럭에 값 제공
	// CBC 모드 : 블럭암호화시 앞 블럭의 암호문이 뒤 블럭의 암호화에 영향을 줌
	// 패딩방법 : 마지막블럭의 자리수를 지정된 블럭의 크기만큼 채워주기 위한 방법 설정
	private final static byte[] iv = new byte[] {
			(byte)0x8E,0x12,0x39,(byte)0x9,
			0x07,0x72,0x6F,(byte)0x5A,
			(byte)0x8E,0x12,0x39,(byte)0x9,
			0x07,0x72,0x6F,(byte)0x5A
	};
	static Cipher cipher; // 암호 처리를 위한 객체 
	static {
		try {
									// 알고리즘/블럭암호화모드/패딩방법
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static byte[] getRandomKey(String algo) throws NoSuchAlgorithmException {
		// algo : 암호 알고리즘 이름 => AES
		// keyGen : 암호 알고리즘에 맞는 키 생성을 위한 객체
		KeyGenerator keyGen = KeyGenerator.getInstance(algo);
		keyGen.init(128);// AES 알고리즘 키크기 : 128~196 비트 크기 가능.
		SecretKey key = keyGen.generateKey(); // keyGen 객체에 설정된 내용으로 키 생성
		return key.getEncoded();// byte 형태로 리턴 
	}
	//========== CipherMain1
	public static String encrypt(String plain) {
		// plain : 암호화를 위한 문자열
		byte[] cipherMsg = new byte[1024];
		try {
			// 대칭키 : 암호화,복호화키가 동일
			randomKey = getRandomKey("AES");
			// AES 알고리즘에서 사용한 Key 객체로 생성
			Key key = new SecretKeySpec(randomKey, "AES");
			// CBC 방식에서 사용한 초기화 벡터값을 설정
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			// Cipher.ENCRYPT_MODE(암호화) 키, IV 설정
			cipher.init(Cipher.ENCRYPT_MODE,key,paramSpec);// 암호화를 위한 cipher 객체
			//cipher.doFinal(plain.getBytes()) : plain 을 byte형태로 암호화 해라 
			cipherMsg = cipher.doFinal(plain.getBytes());// 암호화 실행
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg).trim(); // 문자열로 암호문 리턴
	}
	// byte[] 데이터 => 16진수값을 가진 문자열 형태
	private static String byteToHex(byte[] cipherMsg) {
		if(cipherMsg == null ) return null;
		String str ="";
		for(byte b : cipherMsg) {
			str += String.format("%02X", b);// 각 바이트를 2자리 16 진수로 생성
		}
		return str;
	}
	//cipherMsg : 4BF4CE2F4166ACE288178...
	// 암호화된 데이터를 평문으로 리턴
	public static String decrypt(String cipherMsg) {
		byte[] plainMsg = new byte[1024];
		try {
			// randomKey : 암호화에서 사용된 키값
			// AES 알고리즘에서 사용할 키 객체로 생성
			Key key = new SecretKeySpec(randomKey, "AES");
			// CBC 모드에서 사용할 IV 설정
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			// cipher.DECRYPT_MODE : 복호화 기능
			cipher.init(cipher.DECRYPT_MODE,key,paramSpec); // 복호화 객체 설정
			plainMsg = cipher.doFinal(hexToByte(cipherMsg.trim())); // 복호화 실행
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim(); // byte[]형태의 평문 => 문자열
	}
	// 암호화된 문자열 => byte[] 값
	private static byte[] hexToByte(String str) {
		// str : 074046B62... 두개의 데이터가 1byte
		if(str == null || str.length() < 2) return null; // 잘못된 데이터 
		int len =str.length() / 2; // 2개의 데이터가 1byte
		byte[] buf = new byte[len]; // 7,.. 
		for(int i =0; i<len; i++) {
			buf[i] = (byte)Integer.parseInt(str.substring(i*2,i*2+2),16);
		}
		return buf;
	}
	//======== CipherMain2 
	public static String encrypt(String plain, String key) {
		byte[] cipherMsg = new byte[1024];
		try {								 // byte[] , 알고리즘
			Key genkey = new SecretKeySpec(makeKey(key), "AES");// 128비트 크기
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE,genkey,paramSpec); 
			cipherMsg = cipher.doFinal(plain.getBytes()); // 암호문
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg).trim(); // 문자열로 암호문 리턴
	}
	// AES 알고리즘의 크기 : 128비트 => 16바이트
	// 					128비트의 크기로 변경
	private static byte[] makeKey(String key) {
		// key 값 : abc1234567
		int len = key.length(); //10자리
		char ch ='A';
		for(int i=len; i< 16; i++) {// 16바이트로 생성
			key += ch++; // abc1234567ABCDEF 
		}
		return key.substring(0,16).getBytes(); //16바이트로 생성
	}
	public static String decrypt(String cipher1, String key) {
		byte[] plainMsg = new byte[1024];
		try {								 // byte[] , 알고리즘
			Key genkey = new SecretKeySpec(makeKey(key), "AES"); // 128비트 크기
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE,genkey,paramSpec); 
			plainMsg = cipher.doFinal(hexToByte(cipher1.trim()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();
	}
	public static String makehash(String msg) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] plain = msg.getBytes();
		byte[] hash = md.digest(plain);
		return byteToHex(hash);
	}
	public static void encryptFile(String plainFile, String cipherFile, String strkey) {
		// plainFile : 입력파일. 평문파일. 암호화할 파일
		// cipherFile : 출력파일. 암호문파일
		// strkey : 키값
		try {
			getKey(strkey); // key.ser 파일에 키객체 저장
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("key.ser"));
			Key key = (Key)ois.readObject(); // key.ser 파일에 등록된 Key 객체를 읽어옴				
			ois.close();
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec); // 암호화 객체 초기화
			FileInputStream fis = new FileInputStream(plainFile);// 평문파일
			FileOutputStream fos = new FileOutputStream(cipherFile);// 암호문파일
			//CipherOutputStream : cipher객체에 설정된 내용(암호화)으로 출력하는 스트림
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			byte[] buf = new byte[1024];
			int len;
			while((len= fis.read(buf)) != -1) {
				// cipher 객체에 설정된 내용으로 암호화 하여 fos 스트림에 출력
				cos.write(buf,0,len);
			}
			fis.close(); cos.flush(); fos.flush();
			cos.close(); fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 입력된 키값을 가진 key객체를 파일에 생성.
	private static void getKey(String key) throws Exception, IOException {
		//makeKey(key) : 128비트의 키로 생성
		Key genkey = new SecretKeySpec(makeKey(key),"AES");
		//key.ser이름의 파일 생성
		//ObjectOutputStream : 객체를 외부 전송 스트림
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("key.ser"));
		out.writeObject(genkey);
		out.flush(); out.close();
		
		
	}
	public static void decryptFile(String cipherFile, String plainFile) {
		//cipherFile : 암호화된 데이터를 저장하고 있는 암호화 파일이름
		//plainFile : 복호화된 데이터를 저장 할 출력파일
		try {
			ObjectInputStream ois = 
					new ObjectInputStream(new FileInputStream("key.ser"));
			Key key = (Key)ois.readObject(); // key.ser 파일에서 키객체 읽기
			ois.close();
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE,key, paramSpec);// 암호화객체 초기화 : 복호화기능
			FileInputStream fis = new FileInputStream(cipherFile);
			FileOutputStream fos = new FileOutputStream(plainFile);
			// cipher 객체에 설정된 내용(복호화기능)으로 fos 스트림에 출력 
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			byte[] buf = new byte[1024];
			int len;
			while((len = fis.read(buf)) != -1) {
				cos.write(buf,0,len); // 복호화된 데이터를 fos에 출력
			}
			fis.close(); cos.flush(); fos.flush();
			cos.close(); fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
