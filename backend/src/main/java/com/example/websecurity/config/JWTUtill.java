package com.example.websecurity.config;


import com.example.websecurity.dao.UserDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JWTUtill {
    @Autowired
    private UserDao userDao;

    private String SECRET_KEY = "(Wx%;i-Pr&SyYQ1MPKTU33GqGKg/WQ{t\"SkjB3`[2ZJ,T\"jWW9q}P+p9jtr%Hy{";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public String generateToken(UserDetails userDetails) {
        System.out.println(userDetails);
        Map<String, Object> claims = new HashMap<>();
        claims.put("permissions", "adminPermission");
        return createToken(claims, userDetails.getUsername());
    }


    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean validateTokenUser(String token) {
        try {
            extractAllClaims(token);
            boolean isExpired = isTokenExpired(token);
            System.out.println("is expired" + isExpired);
            return !isExpired;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean validateTokenAdmin(String token) {
        try {
            extractAllClaims(token);
            boolean isExpired = isTokenExpired(token);
            boolean isAdmin = userDao.findIfUserIsAdmin(extractUsername(token));
            System.out.println("is expired" + isExpired);
            System.out.println("is admin" + isAdmin);
            return !isExpired && isAdmin;
        } catch (Exception e) {
            return false;
        }
    }


}
