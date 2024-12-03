package com.ttruongdev.hotelmanagement.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

public class JWT {

    private static final long EXPIRATION_TIME = 1000 * 60 * 24;
    private final SecretKey key;

    public JWT() {
        String secrect = "1766d6f18acc84cabe9ad6f22e0ed1b7fd52c72f97157e481f17b309a8cbc3c8d3ad4dc8efa1a76c98e135bfe09d117960a0551033fad1456d919c18bae1b9228124d3f20fdd3f0713379af346ed7b07badcdd54f1500fde83de92f447978455bc9d938ea620c28af1b43c61cd6796f44d6baefa72dc7f19b58dfe51713edd5cf89d2a10fe08b08b540c870a23f1bc81030588ccd5df28e28650c9a6205d2a8ec1783c491291503510f02718d4fd4cb837a6f6af076f53009894b3d6340cf18014642d9dc8b514b859d67fc52e2e232580ae4dd36a300c0cd06096ad18a6a492d604ff6cc158262f4c2c816d917ad6fbeee8a370e74eb3fe066b0704bc66d302";
        byte[] keyBytes = Base64.getDecoder().decode(secrect.getBytes(StandardCharsets.UTF_8));
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");

    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();

    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }


}
