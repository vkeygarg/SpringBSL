package com.brcm.poc.security.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.brcm.poc.security.Http401UnauthorizedEntryPoint;
import com.brcm.poc.security.jwt.JWTConfigurer;
import com.brcm.poc.security.jwt.TokenProvider;



@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final AuthenticationProvider customAuthPrvdr;

    private final TokenProvider tokenProvider;

   // private final CorsFilter corsFilter;

	private final ConfigProperties configProperties;

    public SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider tokenProvider, AuthenticationProvider customAuthPrvdr
    		//,CorsFilter corsFilter
    		) {

        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.customAuthPrvdr = customAuthPrvdr;
        this.tokenProvider = tokenProvider;
        //this.corsFilter = corsFilter;
        this.configProperties = tokenProvider.getConfig();
    }

    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder
            		.authenticationProvider(customAuthPrvdr)
                //.userDetailsService(userDetailsService)
                   // .passwordEncoder(passwordEncoder())
                    ;
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

    @Bean
    public Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint() {
        return new Http401UnauthorizedEntryPoint();
    }
    
    /*@Bean
	EmbeddedServletContainerCustomizer containerCustomizer() throws Exception {
	    return (ConfigurableEmbeddedServletContainer container) -> {
	        if (container instanceof TomcatEmbeddedServletContainerFactory) {
	            TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
	            tomcat.addConnectorCustomizers(
	                    (connector) -> {
	                        connector.setMaxPostSize(100000000); // 100 MB
	                    }
	            );
	        }
	    };
	}*/
    
    /*@Bean
	public MultipartResolver multipartResolver() {
	    CommonsMultipartResolver multipartResolver
	      = new CommonsMultipartResolver();
	    multipartResolver.setMaxUploadSize(52428800);
	    return multipartResolver;
	}*/

    /*@Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = configProperties.getCors();
        if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
            //log.debug("Registering CORS filter");
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/v2/api-docs", config);
        }
        return new CorsFilter(source);
    }*/
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
      return authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
           // .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(http401UnauthorizedEntryPoint())
        .and()
            .csrf()
            .disable()
            .headers()
            .frameOptions()
            .disable()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/supportlink/login").permitAll()
            .antMatchers("/supportlink/*").authenticated()
            .antMatchers("/supportlink/**").authenticated()
         .and()
         	.logout().logoutRequestMatcher(new AntPathRequestMatcher("/supportlink/logout"))
        .and()
            .apply(securityConfigurerAdapter());

    }
    
    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
      //builder.userDetailsService(userDetailsService);
    		builder.authenticationProvider(customAuthPrvdr);
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
    
 

   
    public static void main(String [] args) {
    	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    	String hashedPassword = passwordEncoder.encode("password");
    	System.out.println("hashedPassword -> " + hashedPassword);
    	System.out.println(BCrypt.checkpw("password", hashedPassword));
    }
}
