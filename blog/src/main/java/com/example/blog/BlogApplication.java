package com.example.blog;

import com.example.blog.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class BlogApplication {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	Faker faker;

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Bean
	public ApplicationRunner configure(UserRepository userRepository ) {
		return args -> {

		};
	}
}
