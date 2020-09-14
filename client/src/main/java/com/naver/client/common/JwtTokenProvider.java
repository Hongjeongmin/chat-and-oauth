package com.naver.client.common;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {
	@Value("${spring.jwt.secret}")
	private String secretKey;

	//TODO TEST 시간이라 Jwt 토큰 유효시간 크게 해놧음.
	private long tokenValidMilisecond = 1000L * 60 * 60 * 30; // 1시간만 토큰 유효

	/**
	 * 이름으로 Jwt Token을 생성한다.
	 */
	public String generateToken(String name) {
		Date now = new Date();
		return Jwts.builder().setId(name).setIssuedAt(now) // 토큰 발행일자
				.setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // 유효시간 설정
				.signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
				.compact();
	}

	/**
	 * Jwt Token을 복호화 하여 이름을 얻는다.
	 */
	public String getUserNameFromJwt(String jwt) {
		return getClaims(jwt).getBody().getId();
	}

	/**
	 * Jwt Token의 유효성을 체크한다.
	 */
	public boolean validateToken(String jwt) {
		return this.getClaims(jwt) != null;
	}

	private Jws<Claims> getClaims(String jwt) {
		try {
			return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
		} catch (SignatureException ex) {
			log.error("Invalid JWT signature");
			throw ex;
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
			throw ex;
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
			throw ex;
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
			throw ex;
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
			throw ex;
		}
	}
}