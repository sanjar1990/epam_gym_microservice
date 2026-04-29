package com.epam.gym.service;

import com.epam.gym.dto.JwtDTO;
import com.epam.gym.enums.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;


@Service
public class JwtTokenService {
    @Value("${jwt.expiration.time}")
    private long DEFAULT_EXPIRATION_MS; // 3 days
    @Value("${secret.key}")
    private String secretkey;

    public JwtTokenService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String encode(String username, List<UserRoleEnum> roles) {
        List<String> roleNames = new ArrayList<>();
        for (UserRoleEnum role : roles) {
            roleNames.add(role.name());
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roleNames);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + DEFAULT_EXPIRATION_MS))
                .and()
                .signWith(getKey())
                .compact();
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtDTO decode(String token) {
        Claims claims = extractAllClaims(token);
        String username = extractUserName(token);
        if (isTokenExpired(token)) {
            throw new RuntimeException("Token is expired");
        }
        if (username == null) {
            throw new RuntimeException("Username is missing in the token");
        }

        List<String> roleNames = claims.get("roles", List.class);

        List<UserRoleEnum> roles = new ArrayList<>();
        if (roleNames != null) {
            for (String roleName : roleNames) {
                roles.add(UserRoleEnum.valueOf(roleName));
            }
        }

        return new JwtDTO(username, roles);
    }
}