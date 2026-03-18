package txu.report.mainapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
//public class JwtTokenUtil implements Serializable {
//
//	private static final long serialVersionUID = -2550185165626007488L;
//
//	public static final long JWT_TOKEN_VALIDITY = 5*60*60;
//
//	@Value("${jwt.secret}")
//	private String secret;
//
//	public String getUsernameFromToken(String token) {
//		return getClaimFromToken(token, Claims::getSubject);
//	}
//
//	public Date getIssuedAtDateFromToken(String token) {
//		return getClaimFromToken(token, Claims::getIssuedAt);
//	}
//
//	public Date getExpirationDateFromToken(String token) {
//		return getClaimFromToken(token, Claims::getExpiration);
//	}
//
//	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
//		Claims claims = getAllClaimsFromToken(token);
//		return claimsResolver.apply(claims);
//	}
//
//	private Claims getAllClaimsFromToken(String token) {
//		Key key = new SecretKeySpec(secret.getBytes(), "HS256");
//		return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
//	}
//
//	private Boolean isTokenExpired(String token) {
//		Date expiration = getExpirationDateFromToken(token);
//		return expiration.before(new Date());
//	}
//
//	private Boolean ignoreTokenExpiration(String token) {
//		// here you specify tokens, for that the expiration is ignored
//		return false;
//	}
//
//	public String generateToken(UserDetails userDetails) {
//		Map<String, Object> claims = new HashMap<>();
//		return doGenerateToken(claims, userDetails.getUsername());
//	}
//
//	private String doGenerateToken(Map<String, Object> claims, String subject) {
//
//		Key key = new SecretKeySpec(secret.getBytes(), "HS256");
//		// Cần mã hóa secret đúng kiểu mã hóa chữ ký
//		// (chẳng hạn SignatureAlgorithm.HS256) trước ghi gắn cho token.
//		// Tránh gán kiểu string, vì string gán vào được xem như là đã được mã hóa của secret (dẫn đến sai lệch secret mong đợi ban đầu).
//
//		return   Jwts.builder()
//				.setClaims(claims)
//				.setSubject(subject)
//				.setIssuer("txu-iss")	// Giá trị này cung cấp cho KIC dựa vào đó để lấy Secret chưa credentials nào ra để verify token.
//				.setIssuedAt(new Date(System.currentTimeMillis()))
//				.setExpiration(new Date(System.currentTimeMillis() + 259200000)) // # Hết hạn sau 3 ngày
//				.signWith(SignatureAlgorithm.HS256, key)
//				.setHeaderParam("alg", "HS256")
//				.setHeaderParam("typ", "JWT")		// Cần đặt kiểu auth để KIC áp vào verify token thích hợp
//				.compact();
//	}
//
//	public Boolean canTokenBeRefreshed(String token) {
//		return (!isTokenExpired(token) || ignoreTokenExpiration(token));
//	}
//
//	public Boolean validateToken(String token, UserDetails userDetails) {
//		String username = getUsernameFromToken(token);
//		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//	}
}
