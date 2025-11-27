package com.railbit.tcasanalysis.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
@AllArgsConstructor
public class WebSecurityConfiguration {
	private JwtAuthenticationFilter jwtFilter;
	private static final String[] PUBLIC_URL={
			"/tcasapi/login/",
			"/tcasapi/analysis/",
			"/tcasapi/traffic/**",
			"/tcasapi/web/login"
			,"/tcasapi/user/sendRegistrationOtp/"
			,"/tcasapi/user/verifyUser/"
			,"/tcasapi/faultPacket/addFaultyPacket/"
			,"/tcasapi/stationaryPacket/addStationaryPacket/"
			,"/tcasapi/locomovement/addLocoMovementData/"
			,"/tcasapi/v1/auth/**"
			,"/v3/api-docs/**","/api-docs"
			,"/swagger-resources/**"
			,"/swagger-ui/**"
			,"/webjars/**",
			"/qrImages/**",
			"/tcasapi/test",
			"/resources/**",
			"/tcasapi/resources/",
			"/ws"
	};
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(authz -> authz
						.requestMatchers(PUBLIC_URL).permitAll()
						.requestMatchers(HttpMethod.GET,
								"/tcasapi/role/",
								"/tcasapi/division/",
								"/tcasapi/zone/",
								"/tcasapi/firm/",
								"/tcasapi/shed/",
								"/tcasapi/resources/",
								"/tcasapi/designation/").permitAll()
						.anyRequest().authenticated()
				)
//				.sessionManagement(session -> session
//						.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Change to IF_REQUIRED for stateful sessions
//						.maximumSessions(50) // Set maximum sessions per user
//						.maxSessionsPreventsLogin(false) // Prevent new logins when the max sessions are reached
//						.sessionRegistry(sessionRegistry())
//				)
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}
	@Bean
	AuthenticationManager authenticationManager
			(AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	private CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedMethod(HttpMethod.GET);
		config.addAllowedMethod(HttpMethod.POST);
		config.addAllowedMethod(HttpMethod.PUT);
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5); // Up to 5 parallel emails at a time
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(20);
		executor.setThreadNamePrefix("EmailSender-");
		executor.initialize();
		return executor;
	}
	@Bean
	public PasswordEncoder passwordEncoder(){
		return NoOpPasswordEncoder.getInstance();
	}

}
