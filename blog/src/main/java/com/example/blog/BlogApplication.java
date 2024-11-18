package com.example.blog;

import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.PostRedisService;
import com.example.blog.service.RedisService;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	public ApplicationRunner configure(UserRepository userRepository, RedisService redisService, PostRepository postRepository , PostRedisService postRedisService) {
		return args -> {
			redisService.clearVotes();
			List<Post> posts = new ArrayList<>();
			User user_test = new User("test", "test", "+012345");
			userRepository.save(user_test);
			List<User> users = new ArrayList<>();
			for (int i = 1; i <= 10; i++) {
				User user = new User();
				user.setUserName("User " + i);
				user.setPassword(faker.internet().password());
				user.setPhoneNumber(faker.phoneNumber().cellPhone());
				user.setRole("ROLE_USER");
				users.add(user);
			}

			for (int i = 1; i <= 10; i++) {
				Post post = new Post();
				post.setTitle(faker.book().title());
				post.setContent(faker.lorem().paragraph(20));
				post.setCreatedAt(LocalDateTime.now());
				post.setUpdatedAt(LocalDateTime.now());
				posts.add(post);
			}

			Random random = new Random();
			for (int i = 0; i < users.size(); i++) {
				User user = users.get(i);
				System.out.println(user);
				Post post = posts.get(i);
				user.getPosts().add(post);
				post.setAuthor(user);
				userRepository.save(user);
			}

			for (Post post : posts) {
				int numVotes = random.nextInt(users.size());
				for (int j = 0; j < numVotes; j++) {
					Long userId = users.get(random.nextInt(users.size())).getId();
					postRedisService.voteForPost(post.getId(), userId);
				}
			}

//			redisService.saveData("testKey", "Hello, Redis!");
//			System.out.println(redisService.getData("testKey"));
		};
	}
}
