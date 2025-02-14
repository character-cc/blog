package com.example.blog;

import com.example.blog.entity.Category;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.*;
import com.example.blog.service.PostService;
import com.example.blog.service.RedisService;
import com.example.blog.service.UserService;
import com.example.blog.util.GenerateContent;
import com.example.blog.util.KeyForRedis;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@SpringBootApplication
public class BlogApplication {

	private RedisTemplate<String, String> redisTemplate;

	Faker faker;

	private UserRepository userRepository;

	private PostRepository postRepository;

	private UserService userService;

	private CategoryRepository categoryRepository;

	private RedisService redisService;

	private PostService postService;

	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Bean
	public ApplicationRunner configure() {
		return args -> {
//			System.out.println(System.getProperty("user.dir"));
			redisTemplate.getConnectionFactory().getConnection().flushAll();
			Random random = new Random();
			List<String> categoriesFake = Arrays.asList(
					"Java",
					"Spring Boot",
					"Hibernate",
					"Microservices",
					"Kubernetes",
					"Docker",
					"DevOps"
			);
			List<Category> categories = new ArrayList<>();
			for (int i = 0; i < categoriesFake.size(); i++) {
				Category category = new Category();
				category.setName(categoriesFake.get(i));
				categories.add(category);
			}
			User test = new User();
			test.setUsername("test00");
			test.setPassword(passwordEncoder.encode("test00"));
			test.setAvatar("http://localhost/api/images/avatar.png");
			test.setName("test00");
			test.setEmail("test@test.com");
			userRepository.save(test);
			categoryRepository.saveAll(categories);
			List<User> users = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				User user = new User();
				user.setUsername("user" + i + 0);
				user.setAvatar("http://localhost/api/images/avatar.png");
				user.setPassword(passwordEncoder.encode("user" + i + 0));
				user.setEmail("email" + i);
				user.setName("name" + i);
				user.setProvider("Keycloak");
				users.add(user);
			}
			userRepository.saveAll(users);
			List<Post> posts = new ArrayList<>();
			List<Comment> comments = new ArrayList<>();
			for (User user : users) {
				for (int i = 0; i < 10; i++) {
					Post post = new Post();
					post.setTitle(faker.lorem().sentence());
					Random r = new Random();
					post.setContent(GenerateContent.contentList.get(r.nextInt(GenerateContent.contentList.size())));
					post.setAuthor(user);
					post.setCreatedAt(LocalDateTime.now());
					for (int c = 0; c < random.nextInt(categories.size()); c++) {
						post.getCategories().add(categories.get(random.nextInt(categories.size())));
					}
					posts.add(post);
					for (int c = 0; c < random.nextInt(users.size()); c++) {
						post.getLikes().add(users.get(random.nextInt(users.size())));
					}
					for (int j = 0; j < 5; j++) {
						Comment comment = new Comment();
						comment.setContent(faker.lorem().sentence());
						comment.setUser(users.get(faker.number().numberBetween(0, users.size())));
						comment.setPost(post);
						post.getComments().add(comment);
						comment.setCreatedAt(LocalDateTime.now());
						for(int z = 0; z < random.nextInt(users.size()); z++) {
							comment.getLikes().add(users.get(faker.number().numberBetween(0, users.size())));
						}
						comments.add(comment);
					}
				}
			}
			postRepository.saveAll(posts);
			for(Post post : posts) {
				for(User user : post.getLikes()) {
					redisService.addUserIdToPostView(post.getId(), user.getId());
				}
				for (User user : users) {
					int r = random.nextInt(users.size());
					if(r % 2 == 0){
						redisService.addUserIdToPostView(post.getId(), user.getId());					}
				}
			}
			postService.recalculatePostScores();
			Set<String> categoriesSet = new HashSet<>();
			categoriesSet.add(KeyForRedis.getKeyForCategory("Java"));

		};
	}
}
