package com.example.blog;

import com.example.blog.entity.Category;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.*;
import com.example.blog.service.PostService;
import com.example.blog.util.KeyForRedis;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
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
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@SpringBootApplication
public class BlogApplication {

	private RedisTemplate<String, Object> redisTemplate;

	Faker faker;

	private UserRepository userRepository;

	private PostRepository postRepository;

	private CommentRepository commentRepository;

	private CategoryRepository categoryRepository;

	private NotificationRepository notificationRepository;

	private PostService postService;


	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Bean
	public ApplicationRunner configure() {
		return args -> {
			System.out.println(System.getProperty("user.dir"));
			redisTemplate.getConnectionFactory().getConnection().flushAll();
			Random random = new Random();
			List<Category> categories = new ArrayList<>();
			for (int i = 0; i < 20; i++) {
				Category category = new Category();
				category.setName("Thể loại" + i);
				categories.add(category);
			}
			categoryRepository.saveAll(categories);
			List<User> users = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				User user = new User();
				user.setUserName(faker.name().username());
				user.setEmail(faker.internet().emailAddress());
				user.setProvider("Keycloak");
				users.add(user);
			}
			userRepository.saveAll(users);
			List<Post> posts = new ArrayList<>();
			List<Comment> comments = new ArrayList<>();
			for (User user : users) {
				for (int i = 0; i < 3; i++) {
					Post post = new Post();
					post.setTitle(faker.lorem().sentence());
					post.setContent(faker.lorem().paragraph());
					post.setAuthor(user);
					post.setCreatedAt(LocalDateTime.now());
					for (int c = 0; c < random.nextInt(categories.size()); c++) {
						post.getCategories().add(categories.get(random.nextInt(categories.size())));
					}
					posts.add(post);
					for (int c = 0; c < random.nextInt(users.size()); c++) {
						post.getLikePost().add(users.get(random.nextInt(users.size())));
					}
					int totalComments = random.nextInt(10);
					for (int j = 0; j < totalComments; j++) {
						Comment comment = new Comment();
						comment.setContent(faker.lorem().sentence());
						comment.setUser(users.get(faker.number().numberBetween(0, users.size())));
						comment.setPost(post);
						post.getComments().add(comment);
						comment.setCreatedAt(LocalDateTime.now());
						for(int z = 0; z < random.nextInt(8); z++) {
							comment.getLikeComment().add(users.get(faker.number().numberBetween(0, users.size())));
						}
						comments.add(comment);
					}
				}
			}
			postRepository.saveAll(posts);
			commentRepository.saveAll(comments);
			for(Post post : posts) {
				for(User user : post.getLikePost()) {
					redisTemplate.opsForSet().add(KeyForRedis.getKeyForPostView(post.getId().toString()) , user.getId());
				}
				for (User user : users) {
					int r = random.nextInt(users.size());
					if(r % 2 == 0){
							redisTemplate.opsForSet().add(KeyForRedis.getKeyForPostView(post.getId().toString()) , user.getId());
					}
				}
				redisTemplate.expire(KeyForRedis.getKeyForPostView(post.getId().toString()),7 , TimeUnit.DAYS);
			}
			postService.recalculatePostScores();
		};
	}
}
