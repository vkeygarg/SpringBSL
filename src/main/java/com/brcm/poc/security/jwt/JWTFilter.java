package com.brcm.poc.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;


public class JWTFilter extends GenericFilterBean{

	   private TokenProvider tokenProvider;

	    public JWTFilter(TokenProvider tokenProvider) {
	        this.tokenProvider = tokenProvider;
	    }

	    @Override
	    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
	        throws IOException, ServletException {
	        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
	        System.out.println("in filter: ");
	        String jwt = resolveToken(httpServletRequest);
	        if (StringUtils.hasText(jwt) && this.tokenProvider.validateToken(jwt)) {
	            Authentication authentication = this.tokenProvider.getAuthentication(jwt);
	            
	            String swNum = authentication.getName();
	            System.out.println("in Filter: authNAme: "+swNum);
	            
	            System.out.println("in Filter: userAgent: "+httpServletRequest.getHeader("User-Agent"));
	            
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        }
	        System.out.println("in filter: token validated");
	        filterChain.doFilter(servletRequest, servletResponse);
	    }

	    private String resolveToken(HttpServletRequest request){
	        String bearerToken = request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER);
	        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
	            return bearerToken.substring(7, bearerToken.length());
	        }
	        String jwt = request.getParameter(JWTConfigurer.AUTHORIZATION_TOKEN);
	        if (StringUtils.hasText(jwt)) {
	            return jwt;
	        }
	        return null;
	    }
}
