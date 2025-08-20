package com.efforttracker.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long expirationTime;

    private byte[] getSecretBytes() {
        return secretKey.getBytes();
    }

    public String generateToken(String username) {
        try {
            JWSSigner signer = new MACSigner(getSecretBytes());
            Date now = new Date();
            Date exp = new Date(now.getTime() + expirationTime);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(now)
                    .expirationTime(exp)
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Cannot generate JWT", e);
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpirationTime();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);
            return verifyToken(token)
                    && claims.getSubject().equals(username)
                    && claims.getExpirationTime().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private JWTClaimsSet extractAllClaims(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    private boolean verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(getSecretBytes());
            return signedJWT.verify(verifier);
        } catch (Exception e) {
            return false;
        }
    }
}
