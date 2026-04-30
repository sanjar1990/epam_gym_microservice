package com.epam.gym.workload.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;


@Slf4j
@Service
public class JwtUtil {

    // TODO:
    //  Store in properties
    // DONE
    @Value("${secret.key}")
    private String secretkey;
//    private static String secretkey = "awlfjeqoiphrfeqhrfhhhqwrqwejlfrghjfgyutuurytegrfwwhyrthrtyhthrthyhrtyhrtyhrrtge4e454";

    public JwtUtil() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public  String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private  Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }


    private  boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private  Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private  SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public  void validateToken(String token) {
        String username = extractUserName(token);
        if (isTokenExpired(token)) {
            throw new RuntimeException("Token is expired");
        }
        if (username == null) {
            throw new RuntimeException("Username is missing in the token");
        }
    }
}
