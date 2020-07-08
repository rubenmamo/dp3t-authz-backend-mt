/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.ws.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.Duration;

import org.dpppt.malta.backend.sdk.authz.security.AuthzJwtDecoder;
import org.dpppt.malta.backend.sdk.authz.security.JWTValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
@EnableWebSecurity
@Profile(value = "jwt")
public class JWTConfig {

	@Order(1)
	public static class RedeemConfig extends WebSecurityConfigurerAdapter {

		@Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.csrf().disable()
			.cors()
	        .and()
            .antMatcher("/v1/codes/redeemed/*")            
	          .authorizeRequests()
	          	.anyRequest().hasAuthority("REDEEM_COVID_CODE")
	          .and()
	        	.httpBasic()
	        	.realmName("CovidAlertMalta");
			// @formatter:on
		}
		
		@Value("${authz.redeem.credentials.username:testuser}")
		String username;
		@Value("${authz.redeem.credentials.password:password}")
		String password;
		
		@Bean
		@Override
		public UserDetailsService userDetailsService() {
			UserDetails user =
				 User.withUsername(username)
					.password(passwordEncoder().encode(password))
					.authorities("REDEEM_COVID_CODE")
					.build();

			return new InMemoryUserDetailsManager(user);
		}
		
	}
	
	@Order(2)
	public static class WSJWTConfig extends WebSecurityConfigurerAdapter {

		@Value("${ws.app.jwt.jwkUrl}")
		String jwkUrl;

		@Value("${ws.app.jwt.maxValidityMinutes: 60}")
		int maxValidityMinutes;

		@Value("${authz.jwt.validator.audience}")
		String jwtValidatorAud;
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
	// @formatter:off
		http
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.csrf().disable()
		.cors()
        .and()
          .authorizeRequests()
            .antMatchers("/v1/codes","/v1/codes/*","/v1/codes/revoked/*")            
            .authenticated()
        .and()
          .oauth2ResourceServer()
          .jwt();
	// @formatter:on
		}

		@Bean
		public JWTValidator jwtValidator() {
			return new JWTValidator(Duration.ofMinutes(maxValidityMinutes), jwtValidatorAud);
		}


		@Bean
		@Primary
		public AuthzJwtDecoder jwtDecoder() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException,
				ParseException {
			
			AuthzJwtDecoder jwtDecoder = new AuthzJwtDecoder(jwkUrl);

			OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefault();
			jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaultValidators, jwtValidator()));
			return jwtDecoder;
			
		}
	}

}