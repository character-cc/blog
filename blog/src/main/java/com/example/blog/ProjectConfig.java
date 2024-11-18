package com.example.blog;

import com.example.blog.service.CustomAuthenticationProvider;
import com.example.blog.service.CustomUserDetailService;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
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
import java.time.Duration;

@Configuration
@EnableCaching
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ProjectConfig {

    @Autowired
    DataSource dataSource;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(c -> {
            c.requestMatchers("/create" ,"/mypost").authenticated();
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

//        httpSecurity.csrf(c -> c.disable());
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
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
//        config.setHostName("redis-18961.c11.us-east-1-2.ec2.redns.redis-cloud.com");
//        config.setPort(18961);
//        config.setPassword("BcCxloIHjTzp6q459Zksi0NcQ3brKoA5");
//        return new LettuceConnectionFactory(config);
//    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Tùy chỉnh cấu hình cache với Redis
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60)) // thời gian tồn tại của cache
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public Faker faker() {
        return new Faker();
    }

    @Bean
    public String POST_VOTE_KEY_PREFIX(){
        return "post:votes:";
    }

    @Bean
    public String USER_VOTE_KEY_PREFIX(){
        return "user:votes:";
    }

    @Bean
    public String POST_VOTE_COUNT_KEY(){
        return "post:voteCounts";
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
