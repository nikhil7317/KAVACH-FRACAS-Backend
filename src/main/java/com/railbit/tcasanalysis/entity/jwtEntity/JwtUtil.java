package com.railbit.tcasanalysis.entity.jwtEntity;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Key;
import java.util.*;
import java.util.function.Function;


@Component
public class JwtUtil implements Serializable {

    private static final String secret="a1c71584849730ef6be865ff25bfe505dee832528aedf5a852beb627354635b64ea2c8bfc5f0001a7c0b2bee4175dc6b2d2a4c4cd170d4c249d233cd34062da6";
    private static final long jtv=1000 * 60 * 60 * 24 * 7;
//    private static final long jtv=1000 * 30;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        return doGenerateToken(userDetails.getUsername());
    }


    public String doGenerateToken(String subject) {

        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("scopes", List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")));
//
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime()+jtv))
                .signWith(key(),SignatureAlgorithm.HS256)
                .compact();
    }
private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
}
  /*  public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (
                username.equals(userDetails.getUsername())
                        && !isTokenExpired(token));
    }*/
public boolean validateToken(String authToken){
    try {
        Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
        return true;
    }catch(MalformedJwtException | IllegalFormatException e){
        System.out.println(e.toString());
    }catch (ExpiredJwtException | UnsupportedJwtException e){
        System.out.println("JWT Expired"+e);
    }
    return false;
}
}