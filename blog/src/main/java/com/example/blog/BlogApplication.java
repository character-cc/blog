package com.example.blog;

import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Bean
	public ApplicationRunner configure(UserRepository userRepository) {
		return env ->
		{
			User user = new User("test" , "test" ,"+012345" );
			userRepository.save(user);
		};
	}
}
