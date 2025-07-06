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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ClerkJwtAuthFilter extends OncePerRequestFilter {

    @Value("${clerk.issuer}")
    private String clerkIssuer;

    private final ClerkJwksProvider jwksProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("🔍 Incoming request: " + method + " " + uri);

        // ✅ Skip these URIs (user sync + public routes + webhooks)
        if (
                uri.startsWith("/api/webhooks") ||
                        uri.startsWith("/api/users/public")
        ) {
            System.out.println("🧠 Skipping ClerkJwtAuthFilter for: " + method + " " + uri);
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Require token for everything else
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Missing or invalid Authorization header for: " + method + " " + uri);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authorization header missing or invalid");
            return;
        }

        try {
            String token = authHeader.substring(7); // remove "Bearer "
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                throw new RuntimeException("Invalid JWT structure");
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(chunks[0]));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode headerNode = mapper.readTree(headerJson);

            if (!headerNode.has("kid")) {
                throw new RuntimeException("JWT header missing 'kid'");
            }

            String kid = headerNode.get("kid").asText();
            PublicKey publicKey = jwksProvider.getPublicKey(kid);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(60)
                    .requireIssuer(clerkIssuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String clerkUserId = claims.getSubject();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            clerkUserId,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println("✅ JWT verified for Clerk user: " + clerkUserId + " (for " + method + " " + uri + ")");

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            System.out.println("❌ JWT verification failed for " + method + " " + uri + ": " + e.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT token: " + e.getMessage());
        }
    }
}
