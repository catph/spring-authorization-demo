package io.hexin.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import io.hexin.bean.User;
import io.hexin.config.Constant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	
	@Value("${spring.profiles.active}")
    private String profiles;
	
	/**
	 * 由字符串生成加密key
	 * @return
	 */
	public SecretKey generalKey(){
		String stringKey = profiles+Constant.JWT_SECRET;
		byte[] encodedKey = Base64.decodeBase64(stringKey);
	    SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	    return key;
	}

	/**
	 * 创建jwt
	 * @param id
	 * @param subject
	 * @param ttlMillis
	 * @return
	 * @throws Exception
	 */
	public  String createJWT(String id, String subject, long ttlMillis) throws Exception {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		SecretKey key = generalKey();
		JwtBuilder builder = Jwts.builder()
//			.setId(id)
//			.setIssuedAt(now)
			.setSubject(subject)
		    .signWith(signatureAlgorithm, key);
//		if (ttlMillis >= 0) {
//		    long expMillis = nowMillis + ttlMillis;
//		    Date exp = new Date(expMillis);
//		    builder.setExpiration(exp);
//		}
		return builder.compact();
	}
	
	/**
	 * 解密jwt
	 * @param jwt
	 * @return
	 * @throws Exception
	 */
	public Claims parseJWT(String jwt) throws Exception{
		SecretKey key = generalKey();
		Claims claims = Jwts.parser()         
		   .setSigningKey(key)
		   .parseClaimsJws(jwt).getBody();
		return claims;
	}
	
	/**
	 * 生成subject信息
	 * @param user
	 * @return
	 */
	public static String generalSubject(User user){
		JSONObject jo = new JSONObject();
		jo.put("userId", user.getUserId());
		jo.put("roleId", user.getRoleId());
		return jo.toJSONString();
	}
	// 解密
	public static String decodeBase64(String s) throws UnsupportedEncodingException {
		byte[] asBytes = java.util.Base64.getDecoder().decode(s);
		return new String(asBytes, "utf-8");
	}

	public static void main(String[] args){
		JwtUtil jwtUtil = new JwtUtil();
//		User user = new User();
//		user.setAccount("111");
//		user.setPwd("111");
//		user.setRoleId(111l);
//		user.setUserId(111l);
//		String subject = jwtUtil.generalSubject(user);
//		try {
//			System.out.println(jwtUtil.createJWT(Constant.JWT_ID, subject, Constant.JWT_TTL));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		try {
			Claims claims =  jwtUtil.parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJqd3QiLCJpYXQiOjE0OTc1MzY5NTYsInN1YiI6IntcInJvbGVJZFwiOjExMSxcInVzZXJJZFwiOjExMX0iLCJleHAiOjE0OTc1NDA1NTZ9.QgCGiDCsKdqXr2uH9plfh7CRPlMtqkWL5R34BsL8pTA");
//			System.out.println("ID: " + claims.getId());
//			System.out.println("Subject: " + claims.getSubject());
//			System.out.println("Issuer: " + claims.getIssuer());
//			System.out.println("Expiration: " + claims.getExpiration());
			System.out.println("header= " +decodeBase64("eyJhbGciOiJIUzI1NiJ9"));
			System.out.println("body= " +decodeBase64("eyJzdWIiOiJ7XCJyb2xlSWRcIjoxMTEsXCJ1c2VySWRcIjoxMTF9In0"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
