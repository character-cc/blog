package com.example.blog;

import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.RedisService;
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

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Bean
	public ApplicationRunner configure(UserRepository userRepository , RedisService redisService , PostRepository postRepository) {
		return env ->
		{
//			redisService.clearVotes();

			List<Post> posts = new ArrayList<>();
			User user_test = new User("test" , "test" ,"+012345" );
			userRepository.save(user_test);
			List<User> users = new ArrayList<>();
			for (int i = 1; i <= 10; i++) {
				User user = new User();
				user.setUserName("user" + i);
				user.setPassword("password" + i); // Bạn nên mã hóa mật khẩu trong thực tế
				user.setPhoneNumber("012345678" + i);
				user.setRole("ROLE_USER");
				users.add(user);
			}
			Random random = new Random();
			for (int i = 1; i <= 10; i++) {
				Post post = new Post();
				post.setTitle("Bài viết số " + i);
				post.setContent(longRandomContents[random.nextInt(longRandomContents.length)]);
				post.setCreatedAt(LocalDateTime.now());
				post.setUpdatedAt(LocalDateTime.now());
				posts.add(post);
			}

			for(int i = 0 ; i < users.size() ; i++){
				User user = users.get(i);
				System.out.println(user);
				Post post = posts.get(i);
				user.getPosts().add(post);
				post.setAuthor(user);
				userRepository.save(user);
			}

//			for (Post post : posts) {
//				int numVotes = random.nextInt(users.size()); // Random number of votes
//				for (int j = 0; j < numVotes; j++) {
//					Long userId = users.get(random.nextInt(users.size())).getId(); // Get a random user ID
//					redisService.voteForPost(post.getId(), userId); // Add the vote
//				}
//			}

//			redisService.saveData("testKey", "Hello, Redis!");
//			redisService.getData();
//			System.out.println((String) redisService.getData("testKey"));
		};
	}
	// Mảng nội dung bài viết dài
	private final String[] longRandomContents = {
			"Nội dung bài viết 1: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",

			"Nội dung bài viết 2: Quisque volutpat consequat arcu, sit amet interdum libero. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Proin in purus gravida, sagittis velit vitae, fermentum felis. Fusce finibus, erat id suscipit tincidunt, urna quam efficitur orci, ut aliquet purus ex nec magna.",

			"Nội dung bài viết 3: Donec aliquet, metus et scelerisque laoreet, purus arcu cursus dolor, id vehicula libero arcu non nisl. Nulla facilisi. Proin malesuada justo sit amet eros egestas, a sodales dolor gravida. In et purus ut metus consectetur bibendum.",

			"Nội dung bài viết 4: Vivamus suscipit, nisl sit amet hendrerit venenatis, eros neque vestibulum elit, ac vulputate est ante non nulla. Morbi dapibus, augue a tristique sollicitudin, ligula magna gravida urna, non malesuada dui nisi vel arcu.",

			"Nội dung bài viết 5: Mauris ac risus eu velit cursus convallis ac a erat. Aliquam erat volutpat. Donec euismod purus at magna fringilla, nec interdum magna luctus. Nunc nec nisl lacus. Vivamus at metus venenatis, venenatis erat vitae, sollicitudin orci.",

			// Thêm nhiều đoạn văn khác theo định dạng tương tự...

			"Nội dung bài viết 6: Cras sollicitudin nisl ac nisi ullamcorper, sed luctus mi cursus. Vivamus pretium lectus ut erat blandit cursus. Sed sed cursus nulla. Vivamus faucibus ultricies ipsum, vel malesuada purus convallis sit amet.",

			"Nội dung bài viết 7: Integer non tortor finibus, gravida augue sit amet, viverra orci. Quisque rutrum eu libero a elementum. Phasellus tristique massa ut leo facilisis, eu pellentesque eros fringilla. Donec tincidunt turpis vitae magna vestibulum, nec dictum elit tempor.",

			"Nội dung bài viết 8: Suspendisse eu lacus sed lorem blandit pretium. Integer bibendum enim in ex gravida, vitae interdum arcu placerat. Nunc et ipsum lorem. Donec sit amet libero in purus egestas varius.",

			"Nội dung bài viết 9: Sed ac nulla eget lectus lacinia posuere. Nullam scelerisque tristique sem, in pellentesque lacus scelerisque vel. Sed convallis orci non risus faucibus, ut pretium ante consequat.",

			"Nội dung bài viết 10: Proin at quam ut ex sodales tincidunt. Nunc ac libero nec massa faucibus interdum. Sed ac neque sit amet massa venenatis sollicitudin. Suspendisse at mauris id libero auctor dignissim.",

			// Tiếp tục thêm đủ 20 bài viết...
	};
}
