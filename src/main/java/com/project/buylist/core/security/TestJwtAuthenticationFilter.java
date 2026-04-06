// package com.project.buylist.core.security;

// import java.io.IOException;
// import java.util.List;
// import java.util.Objects;

// import org.springframework.context.annotation.Profile;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.oauth2.jwt.Jwt;
// import
// org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
// import org.springframework.stereotype.Component;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// @Profile("!prod")
// public class TestJwtAuthenticationFilter {
// private final Jwt mockJwt;

// public TestJwtAuthenticationFilter(Jwt mockJwt) {
// this.mockJwt = mockJwt;
// }

// public void doFilterInternal(HttpServletRequest request, HttpServletResponse
// response, FilterChain filterChain)
// throws ServletException, IOException {
// if (Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
// List<String> roles = mockJwt.getClaimAsStringList("roles");
// List<SimpleGrantedAuthority> authorities = roles.stream()
// .map(SimpleGrantedAuthority::new)
// .toList();

// JwtAuthenticationToken auth = new JwtAuthenticationToken(mockJwt,
// authorities, mockJwt.getSubject());
// SecurityContextHolder.getContext().setAuthentication(auth);
// }

// filterChain.doFilter(request, response);
// }

// }
