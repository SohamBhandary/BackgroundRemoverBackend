package com.Soham.removeBG.Security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;

@Component
public class ClerkJwtAuthFilter extends OncePerRequestFilter {

    // Inject the 'clerk.issuer' property from application.properties via constructor injection
    private final String clerkIssuer;

    // JwksProvider dependency is injected via constructor
    private final ClerkJwksProvider jwksProvider;

    // Constructor for injecting both clerkIssuer property and jwksProvider bean
    public ClerkJwtAuthFilter(@Value("${clerk.issuer}") String clerkIssuer, ClerkJwksProvider jwksProvider) {
        this.clerkIssuer = clerkIssuer;
        this.jwksProvider = jwksProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("API HITTING ClerkJwtAuthFilter");

        if (request.getRequestURI().contains("/api/webhooks")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract Authorization header
        String authHeader = request.getHeader("Authorization");

        // If header missing or not starting with Bearer, send 403 Forbidden
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authorization header missing/invalid");
            return;
        }

        try {
            // Remove 'Bearer ' prefix to get token string
            String token = authHeader.substring(7);

            // JWT structure is header.payload.signature, split by '.'
            String[] chunks = token.split("\\.");

            // Decode Base64Url the header part to get JSON
            String headerJson = new String(Base64.getUrlDecoder().decode(chunks[0]));

            // Parse JSON header to extract 'kid'
            ObjectMapper mapper = new ObjectMapper();
            JsonNode headerNode = mapper.readTree(headerJson);
            String kid = headerNode.get("kid").asText();

            // Use kid to fetch the correct public key from JWKS provider
            PublicKey publicKey = jwksProvider.getPublicKey(kid);

            // Parse and validate JWT using the public key and issuer
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(60)  // 60 seconds clock skew allowance
                    .requireIssuer(clerkIssuer)       // verify the token issuer matches configured issuer
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Get the subject (user id) from claims
            String clerkUserId = claims.getSubject();

            // Create Authentication token with ROLE_ADMIN authority (adjust roles as needed)
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    clerkUserId,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // Set authentication in SecurityContext for downstream security checks
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // Proceed with next filter in chain
            filterChain.doFilter(request, response);
            System.out.println("completed");
        } catch (Exception e) {
            // If any error occurs (invalid token, parsing issue), send 403 Forbidden response
            System.out.println("Error in ClerkJwtAuthFilter");
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT token");
        }
    }
}
