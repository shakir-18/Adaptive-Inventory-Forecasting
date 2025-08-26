package com.Inventory.InventoryManagement.utility;

import com.Inventory.InventoryManagement.service.CustomDetailsService;
import com.Inventory.InventoryManagement.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    private CustomDetailsService customDetailsService;
    @Autowired
    private TokenBlacklistService blacklistService;
    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        String header=request.getHeader("Authorization");
        String token=null;
        String userId=null;
        if(header!=null && header.startsWith("Bearer "))
        {
            token=header.substring(7);
            if(blacklistService.isBlacklisted(token))
            {
                System.out.println("Blocked request with blacklisted token: "+token);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted, please login again");
                return;
            }
            try {
                userId = jwtUtil.extractUserId(token);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        }

        if(userId!=null && SecurityContextHolder.getContext().getAuthentication()==null)
        {
            UserDetails userDetails=customDetailsService.loadUserByUsername(userId);
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request,response);
    }
}