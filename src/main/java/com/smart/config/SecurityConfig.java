package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	@Bean 
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean 
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}
	

	//Configure method
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
		auth.authenticationProvider(authenticationProvider());
	}
	
	@Bean
	 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		 http.csrf().disable()
		 .authorizeRequests()
         .requestMatchers("/admin/**").hasRole("ADMIN")
         .requestMatchers("/user/**").hasRole("USER")
         .requestMatchers("/**").permitAll()
     .and()
     .formLogin()
         .loginPage("/signin") // loginProcessingUrl(url) if you have to submit the username and password to url
         .permitAll()
         .loginProcessingUrl("/signin")
         .defaultSuccessUrl("/user/index")
         //.failureUrl("/login-fail")
     .and()
     .logout()
     	.logoutUrl("/logout")
         .permitAll();
		 
		 
		 //defaultSuccessUrl() - the landing page after a successful login
		 //failureUrl()- the landing page after a unsuccessful login
		 
		 http.authenticationProvider(authenticationProvider());
		 
		 DefaultSecurityFilterChain defaultSecurityFilterChain=http.build();
		 return defaultSecurityFilterChain;
		 
	 }
	
}
