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

    // Generate token với userId + role
    public String generateToken(String userId, String role) {
        try {
            JWSSigner signer = new MACSigner(getSecretBytes());
            Date now = new Date();
            Date exp = new Date(now.getTime() + expirationTime);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .claim("userId", userId)
                    .claim("role", role)
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

    // Lấy userId (subject) từ token
    public String extractUserId(String token) {
        try {
            return extractAllClaims(token).getStringClaim("userId");
        } catch (Exception e) {
            return extractAllClaims(token).getSubject(); // fallback về subject
        }
    }

    // Lấy role từ token
    public String extractRole(String token) {
        return (String) extractAllClaims(token).getClaim("role");
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpirationTime();
    }

    public boolean isTokenValid(String token, String expectedUserId) {
        try {
            JWTClaimsSet claims = extractAllClaims(token);
            return verifyToken(token)
                    && claims.getStringClaim("userId").equals(expectedUserId)
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
