package com.example.blog;

import com.example.blog.service.CustomAuthenticationProvider;
import com.example.blog.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ProjectConfig {

    @Autowired
    DataSource dataSource;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(c -> {
//            c.requestMatchers("").authenticated();
            c.anyRequest().permitAll();
        });

        httpSecurity.formLogin(c -> {
            c.loginPage("/login").permitAll();
            c.successHandler((request, response, authentication) -> {
                SavedRequest savedRequest = (SavedRequest) request.getSession()
                        .getAttribute("SPRING_SECURITY_SAVED_REQUEST");
                if (savedRequest == null) {
                    response.sendRedirect("/home");
                } else {
                    response.sendRedirect(savedRequest.getRedirectUrl());
                }
            });

            c.failureUrl("/login?error=true");
        });

        httpSecurity.logout(c -> c
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
        );
        httpSecurity.userDetailsService(userDetailsService());
        httpSecurity.authenticationProvider(authenticationProvider());

        httpSecurity.rememberMe(rememberMeConfigurer -> rememberMeConfigurer
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(262000) );
        return httpSecurity.build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    UserDetailsService userDetailsService(){
        return new CustomUserDetailService();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        return new CustomAuthenticationProvider(passwordEncoder(),userDetailsService());
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer()); // Có thể chọn JSON hoặc các serializer khác nếu cần
        return template;
    }
    @Bean
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityContextHolderStrategy securityContextHolderStrategy() {
        return SecurityContextHolder.getContextHolderStrategy();
    }
}
