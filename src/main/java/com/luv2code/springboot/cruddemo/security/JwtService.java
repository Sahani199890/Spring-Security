package com.luv2code.springboot.cruddemo.security;


import com.luv2code.springboot.cruddemo.entity.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class JwtService {

    private static final String SECRET_KEY="jIQ77P02vBRUpVl9QCFWTzwv4tqBrkD/AxxTXQZcWy60YwFzU5XO/8fPwtmCKMEK";

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject );
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    //generate token only with userDetails
    public String generateToken(Employee userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }


    //	generate token  with userDetails and extraClaims
    public String generateToken(Map<String,Object> extraClaims,
                                UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 *60 *24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //check is token is valid
    public boolean isTokenValid(String token,  Employee userDetails) {
        final String email=extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    //check token is expire
    private boolean isTokenExpired(String token) {
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        return extractExpiration(token).before(currentDate);
    }

    private java.sql.Date extractExpiration(String token) {
        return new java.sql.Date(extractClaim(token, Claims::getExpiration).getTime());
    }


//	private boolean isTokenExpired(String token) {
//
//		return extractExpiration(token).before(new java.sql.Date());
//	}
//
//
//
//
//	private java.sql.Date extractExpiration(String token) {
//
//		return extractClaim(token, Claims::getExpiration);
//	}


    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private Key getSignInKey() {
        // TODO Auto-generated method stub
        byte[] KeyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(KeyBytes);
    }



}

