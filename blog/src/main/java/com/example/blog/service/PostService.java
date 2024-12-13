package com.example.blog.service;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.dto.*;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.util.KeyForRedis;
import com.example.blog.util.SessionUserNotAuth;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class PostService {

    private PostRepository postRepository;

    private RedisTemplate<String,Object> redisTemplate;

    private SessionUserNotAuth sessionUserNotAuth;

    private CategoryRepository categoryRepository;

    private UserRepository userRepository;

    private ApplicationContext applicationContext;


    @Scheduled(cron = "0 0 0 * * *")
    public void recalculatePostScores(){
        redisTemplate.delete(KeyForRedis.getKeyForPostScore());
        List<Post> posts = postRepository.findPostByCreatedAtAfter(LocalDateTime.now().minusWeeks(1));
        for (Post post : posts) {
            Long view =  redisTemplate.opsForSet().size(KeyForRedis.getKeyForPostView(post.getId().toString()));
            if(view == null) view = 0L;
            Long like = Long.valueOf(post.getLikePost().size()) ;
            Long comment = Long.valueOf(post.getComments().size()) ;
            redisTemplate.opsForValue().set(KeyForRedis.getKeyForTotalPostComment(post.getId().toString()) , comment );
            redisTemplate.expire(KeyForRedis.getKeyForTotalPostComment(post.getId().toString()),7 , TimeUnit.DAYS);
            redisTemplate.opsForValue().set(KeyForRedis.getKeyForTotalPostLike(post.getId().toString()) , like);
            redisTemplate.expire(KeyForRedis.getKeyForTotalPostLike(post.getId().toString()),7 , TimeUnit.DAYS);
            Long day = ChronoUnit.DAYS.between(post.getCreatedAt(), LocalDateTime.now());
            long score = (view + like * 20 + comment * 200) * (long) Math.exp(day / 10.0);
            post.getCategories().forEach(category -> {
                redisTemplate.opsForSet().add(KeyForRedis.getKeyForCategory(category.getName().replaceAll("\\s+", "")), post.getId());
            });
            redisTemplate.opsForZSet().add(KeyForRedis.getKeyForPostScore(), post.getId().toString(), score);
        }
    }

    public List<PostSummaryDTO> getPostsForCategory(Authentication authentication , HttpServletRequest request , String category){
        try {
            if(authentication == null) if (sessionUserNotAuth.getCategories().isEmpty()) {
                return firstTimeVisit(request, "ForYou");
            } else {
                Set<String> categories = sessionUserNotAuth.getCategories();
                return visitWithCategories(request, categories);
            }
            Set<String> categories = new HashSet<>();
            if(category.equals("ForYou")){
                UserService userService = (UserService) applicationContext.getBean(UserService.class);
                UserDTO userDTO = userService.getUserDTOById(((CustomOidcUser)authentication.getPrincipal()).getUserId());
                categories = userDTO.getCategories();
            }
            else {
                categories.add(category);
            }
            return visitWithCategories(request , categories);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public List<PostSummaryDTO> firstTimeVisit(HttpServletRequest request , String category){
        category = category.replaceAll("\\s+", "");
        Integer positionPost = (Integer) redisTemplate.opsForValue().get(KeyForRedis.getKeyForPositionPost(request.getSession().getId(),category) );
        if(positionPost == null || positionPost > (Long) redisTemplate.opsForZSet().size(KeyForRedis.getKeyForPostScore())) positionPost = 0;
        Set<Object> postIds = redisTemplate.opsForZSet().reverseRange(KeyForRedis.getKeyForPostScore(), positionPost, positionPost + 9);
        List<Long> postIdList = postIds.stream().map(id -> Long.parseLong(id.toString())).collect(Collectors.toList());
        redisTemplate.opsForValue().set(KeyForRedis.getKeyForPositionPost(request.getSession().getId(),category) , positionPost + 10);
        redisTemplate.expire(KeyForRedis.getKeyForPositionPost(request.getSession().getId(),category) , 5 , TimeUnit.HOURS);
        List<PostSummaryDTO> postSummaryDTOSList = postsById(postIdList).stream().map(post -> {
            Long like = (Long) redisTemplate.opsForValue().get(KeyForRedis.getKeyForTotalPostLike(post.getId().toString()));
            Long comment = (Long) redisTemplate.opsForValue().get(KeyForRedis.getKeyForTotalPostComment(post.getId().toString()));
            return PostSummaryDTO.toDTO(post, like, comment);
        }).collect(Collectors.toList());
        return postSummaryDTOSList;
    }

    public List<Post> postsById(List<Long> postIds){
        return postRepository.findAllById(postIds);
    }


    public List<PostSummaryDTO> visitWithCategories(HttpServletRequest request , Set<String> categoriesSet){
        List<String> categories = categoriesSet.stream().map(category -> category.replaceAll("\\s+", "")).collect(Collectors.toList());
        String category = null;
        if(categories.size() == 1) category = categories.get(0);
        else category = "ForYou";
        List<String> categoriesKeyForCategory = categories.stream().map(c -> KeyForRedis.getKeyForCategory(c)).toList();
        Set<Long> interCategories = redisTemplate.opsForSet().intersect(categoriesKeyForCategory).stream().map(Long.class::cast).collect(Collectors.toSet());
        interCategories.forEach(c -> {
            System.out.println("categories: " + c);
        });
        Integer positionPost = (Integer) redisTemplate.opsForValue().get(KeyForRedis.getKeyForPositionPost(request.getSession().getId(),category));
        if(positionPost == null ||  Long.valueOf(positionPost) > (Long) redisTemplate.opsForZSet().size(KeyForRedis.getKeyForPostScore())) positionPost = 0;
        System.out.println("size" + redisTemplate.opsForZSet().size(KeyForRedis.getKeyForPostScore()));
        System.out.println("Vị trí" + positionPost + " kdfl");
        Set<Object> postIds = redisTemplate.opsForZSet().reverseRange(KeyForRedis.getKeyForPostScore(), positionPost, positionPost + 19);
        List<Long> postIdList = postIds.stream().map(id -> Long.parseLong(id.toString())).collect(Collectors.toList());
        List<Long> filteredPostIdList = postIdList.stream()
                .filter(postId -> interCategories.contains(postId)).collect(Collectors.toList());
        redisTemplate.opsForValue().set(KeyForRedis.getKeyForPositionPost(request.getSession().getId(),category) , positionPost + 20);
        redisTemplate.expire(KeyForRedis.getKeyForPositionPost(request.getSession().getId(),category) , 5 , TimeUnit.HOURS);
        List<PostSummaryDTO> postSummaryDTOSList = postsById(filteredPostIdList).stream().map(post -> {
            Long like = (Long) redisTemplate.opsForValue().get(KeyForRedis.getKeyForTotalPostLike(post.getId().toString()));
            Long comment = (Long) redisTemplate.opsForValue().get(KeyForRedis.getKeyForTotalPostComment(post.getId().toString()));
            return PostSummaryDTO.toDTO(post, like, comment);
        }).collect(Collectors.toList());

        postSummaryDTOSList.forEach(c-> {
            System.out.println("list: " + c);
        });
        return postSummaryDTOSList;
    }

    public boolean savePost(PostRequestDTO postRequestDTO , Authentication authentication ,HttpServletRequest request){
        if(authentication != null){
            Post post = new Post();
            post.setTitle(postRequestDTO.getTitle());
            post.setContent(postRequestDTO.getContent());
            Set<Category> categoriesSet = categoryRepository.findAllByName(postRequestDTO.getCategories());
            post.setCategories(categoriesSet);
            User user = userRepository.findUserById(((CustomOidcUser) authentication.getPrincipal()).getUserId());
            post.setAuthor(user);
            Set<Object> images = redisTemplate.opsForSet().members(KeyForRedis.getKeyForUploadImage(request.getSession().getId()));
            List<String> imagesSett = images.stream().map(imageUrl -> String.valueOf(imageUrl)).collect(Collectors.toList());
            post.setImages(imagesSett);
            postRepository.save(post);
            if(post.getId() != null){
                return true;
            }
        }
        return false;

    }

    public PostDetailDTO getPostDetail(Long id){
        Post post = postRepository.findById(id).orElse(null);
        if(post == null) return null;
        post.getComments();
        post.getLikePost();
        return PostDetailDTO.toDTO(post);
    }

    public boolean likePost(Long Postid , Authentication authentication){
        Post post = postRepository.findById(Postid).orElse(null);
        if(post == null) return false;
        System.out.println("Like " + post.getLikePost().size());
        User user = userRepository.findUserById(((CustomOidcUser) authentication.getPrincipal()).getUserId());
        if(post.getLikePost().contains(user)) post.getLikePost().remove(user);
        else post.getLikePost().add(user);
        postRepository.save(post);
        System.out.println("Like " + post.getLikePost().size());
        return true;
    }

    public Set<PostForSideBarDTO> getPostFollowingUser(Authentication authentication , HttpServletRequest request){
        User user = userRepository.findUserById(((CustomOidcUser) authentication.getPrincipal()).getUserId());
        Set<User> followingUserSet = user.getFollowing();
        System.out.println("Set Follow" + followingUserSet.size());
        List<Post> postList = new ArrayList<>();
        for(User u : followingUserSet){
            Pageable pageable = PageRequest.of(0, 1);
            List<Post> posts = postRepository.findLatestPostByUser(u.getId(), pageable);
            System.out.println(posts.size());
            postList.add(posts.get(0));
            System.out.println("List "+ postList.size());
        }
        Random random = new Random();
        Set<PostForSideBarDTO> postForSideBarDTOSet = new HashSet<>();
        for(int i = 0 ; i < 5 ; i++){
            int r = random.nextInt(postList.size());
            Post post = postList.get(r);
            post.getLikePost();
            postForSideBarDTOSet.add(PostForSideBarDTO.toDTO(post));
        }
        return postForSideBarDTOSet;
    }

}
