package com.juicycool.backend.global.security.config;

import com.juicycool.backend.global.security.filter.JwtFilter;
import com.juicycool.backend.global.security.handler.JwtAccessDeniedHandler;
import com.juicycool.backend.global.security.handler.JwtAuthenticationEntryPoint;
import com.juicycool.backend.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)

                .exceptionHandling(exceptionConfig ->
                        exceptionConfig.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

                                // auth
                                .requestMatchers(HttpMethod.POST, "/api/v1/auth/signup").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/auth/signin").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/auth").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/auth").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/auth/health").authenticated()

                                // mail
                                .requestMatchers(HttpMethod.POST, "/api/v1/email").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/email").permitAll()

                                // stock
                                .requestMatchers(HttpMethod.POST, "/api/v1/stock/{stock_id}").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/stock/{stock_id}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/stock").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/v1/stock/sell/{stock_id}").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/v1/stock/buy/{stock_id}").authenticated()

                                // board
                                .requestMatchers(HttpMethod.POST, "/api/v1/board/{community_id}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/board/{board_id}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/board/list/{community_id}").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/board/{board_id}").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/board/{board_id}").authenticated()

                                // reservation
                                .requestMatchers(HttpMethod.GET, "/api/v1/reservation").authenticated()

                                // community
                                .requestMatchers(HttpMethod.GET, "/api/v1/community").authenticated()

                                // comment
                                .requestMatchers(HttpMethod.POST, "/api/v1/comment/{board_id}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/comment/{board_id}").authenticated()

                                // like
                                .requestMatchers(HttpMethod.POST, "/api/v1/like/{board_id}").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/like/{board_id}").authenticated()

                                .anyRequest().denyAll()




                )

                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);


        return http.build();

    }
}