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
import jakarta.persistence.criteria.CriteriaBuilder;
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
            redisTemplate.opsForValue().set(KeyForRedis.getKeyForTotalPostComment(post.getId().toString()) , comment ,7 , TimeUnit.DAYS);
            redisTemplate.opsForValue().set(KeyForRedis.getKeyForTotalPostLike(post.getId().toString()) , like , 7 , TimeUnit.DAYS);
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
            if (authentication == null) {
                if (sessionUserNotAuth.getCategories().isEmpty()) {
                    return firstTimeVisit(request, "ForYou");
                } else {
                    Set<String> categories = sessionUserNotAuth.getCategories();
                    sessionUserNotAuth.getCategories().forEach(c -> System.out.println("Session category " + c));
                    return visitWithCategories(request, categories);
                }
            }
            Set<String> categories = new HashSet<>();
            if(category.equals("ForYou")){
                UserService userService =  applicationContext.getBean(UserService.class);
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
        if(positionPost == null) positionPost = 0;
        Set<Object> postIds = redisTemplate.opsForZSet().reverseRange(KeyForRedis.getKeyForPostScore(), positionPost, positionPost + 9);
        List<Long> postIdList = postIds.stream().map(id -> Long.parseLong(id.toString())).collect(Collectors.toList());
        redisTemplate.opsForValue().set(KeyForRedis.getKeyForPositionPost(request.getSession().getId(),category) , positionPost + 10 , 5 , TimeUnit.HOURS);
        List<PostSummaryDTO> postSummaryDTOSList = postsById(postIdList).stream().map(post -> {
            Long like = (Long) redisTemplate.opsForValue().get(KeyForRedis.getKeyForTotalPostLike(post.getId().toString()));
            Long comment = (Long) redisTemplate.opsForValue().get(KeyForRedis.getKeyForTotalPostComment(post.getId().toString()));
            return PostSummaryDTO.toDTO(post, like, comment);
        }).collect(Collectors.toList());
        return postSummaryDTOSList;
//        if(positionPost > (Long) redisTemplate.opsForZSet().size(KeyForRedis.getKeyForPostScore())){
//
//        }
    }

    public List<Post> postsById(List<Long> postIds){
        return postRepository.findAllById(postIds);
    }


    public List<PostSummaryDTO> visitWithCategories(HttpServletRequest request , Set<String> categoriesSet){
        List<String> categories = categoriesSet.stream().map(category -> category.replaceAll("\\s+", "")).collect(Collectors.toList());
        String category = null;
        if(categories.size() == 1) category = categories.get(0);
        else category = "ForYou";
        if(categories.size() > 1) {
            String category1 = "";
            String category2 = "";
            Integer max = 0;
            for(int i = 0 ; i < categories.size() - 1; i++) {
                for (int j = i + 1 ; j < categories.size() ; j++) {
                    List<String> c = new ArrayList<>();
                    c.add(KeyForRedis.getKeyForCategory(categories.get(i)));
                    c.add(KeyForRedis.getKeyForCategory(categories.get(j)));
                    Integer size = redisTemplate.opsForSet().intersect(c).size();
                    if(size > max){
                        max = size;
                        category1 = categories.get(i);
                        category2 = categories.get(j);
                    }
                }
            }
            categories.clear();
            categories.add(category1);
            categories.add(category2);
        }
        categories.forEach(c ->{
            System.out.println("Category Sau khi lọc: " + c);
        });
        List<String> categoriesKeyForCategory = categories.stream().map(c -> KeyForRedis.getKeyForCategory(c)).toList();
        Set<Long> interCategories = redisTemplate.opsForSet().intersect(categoriesKeyForCategory).stream().map(Long.class::cast).collect(Collectors.toSet());
        interCategories.forEach(c -> {
            System.out.println("categories: " + c);
        });
        Integer positionPost = (Integer) redisTemplate.opsForValue().get(KeyForRedis.getKeyForPositionPost(request.getSession().getId(),category));
        if(positionPost == null ) positionPost = 0;
        System.out.println("size" + redisTemplate.opsForZSet().size(KeyForRedis.getKeyForPostScore()));
        System.out.println("Vị trí" + positionPost + " kdfl");
        Set<Object> postIds = new HashSet<>();
        Long size =  redisTemplate.opsForZSet().size(KeyForRedis.getKeyForPostScore());
        List<Long> filteredPostIdList = new ArrayList<>();
        while (filteredPostIdList.size() < 6 && Long.valueOf(positionPost) < size){
            postIds = (redisTemplate.opsForZSet().reverseRange(KeyForRedis.getKeyForPostScore(), positionPost, positionPost + 10));
            positionPost += 11;
            List<Long> postIdList = postIds.stream().map(id -> Long.parseLong(id.toString())).collect(Collectors.toList());
            filteredPostIdList.addAll(postIdList.stream()
                    .filter(postId -> interCategories.contains(postId)).collect(Collectors.toList()));
            System.out.println("Size     " + filteredPostIdList.size());
        }
        redisTemplate.opsForValue().set(KeyForRedis.getKeyForPositionPost(request.getSession().getId(),category) , positionPost + 11 , 5 , TimeUnit.HOURS);
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

    public Long savePost(PostRequestDTO postRequestDTO , Authentication authentication ,HttpServletRequest request){
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
                redisTemplate.delete(KeyForRedis.getKeyForUploadImage(request.getSession().getId()));
                redisTemplate.opsForSet().add(KeyForRedis.getKeyForPostView(post.getId().toString()) , user.getId());
                redisTemplate.opsForValue().set(KeyForRedis.getKeyForTotalPostComment(post.getId().toString()) , 0L ,7 , TimeUnit.DAYS);
                redisTemplate.opsForValue().set(KeyForRedis.getKeyForTotalPostLike(post.getId().toString()) , 0L , 7 , TimeUnit.DAYS);
                post.getCategories().forEach(category -> {
                    redisTemplate.opsForSet().add(KeyForRedis.getKeyForCategory(category.getName().replaceAll("\\s+", "")), post.getId());
                });
                recalculateScoreAfterUpdatePost(post);
                return post.getId();
            }
        }
        return null;

    }

    public void recalculateScoreAfterUpdatePost(Post post){
        Long likes = (Long) redisTemplate.opsForValue().get(KeyForRedis.getKeyForTotalPostLike(post.getId().toString()));
        Long comments = (Long) redisTemplate.opsForValue().get(KeyForRedis.getKeyForTotalPostComment(post.getId().toString()));
        Long view = (Long) redisTemplate.opsForSet().size(KeyForRedis.getKeyForPostView(post.getId().toString()));
        Long day = ChronoUnit.DAYS.between(post.getCreatedAt(), LocalDateTime.now());
        Long score = (view + likes * 20 + comments * 200) * (long) Math.exp(day / 10.0);
        redisTemplate.opsForZSet().add(KeyForRedis.getKeyForPostScore(), post.getId().toString(), score);
    }

    public PostDetailDTO getPostDetail(Long id , Authentication authentication , HttpServletRequest request){
        Post post = postRepository.findById(id).orElse(null);
        if(post == null) return null;
        post.getComments();
        post.getLikePost();
        if(authentication != null){
            Long userId = ((CustomOidcUser) authentication.getPrincipal()).getUserId();
            redisTemplate.opsForSet().add(KeyForRedis.getKeyForPostView(post.getId().toString()) , userId);
        }
        else {
            post.getCategories().forEach(category -> {
                redisTemplate.opsForSet().add(KeyForRedis.getKeyForFavoriteCategory(category.getName().replaceAll("\\s+", ""), request.getSession().getId()), post.getId());
                if(redisTemplate.opsForSet().size(KeyForRedis.getKeyForFavoriteCategory(category.getName().replaceAll("\\s+", ""), request.getSession().getId()) ) > 0){

                    sessionUserNotAuth.getCategories() .add(category.getName());
                }
            });
        }
        recalculateScoreAfterUpdatePost(post);
        return PostDetailDTO.toDTO(post);
    }

    public boolean likePost(Long Postid , Authentication authentication){
        Post post = postRepository.findById(Postid).orElse(null);
        if(post == null) return false;
        System.out.println("Like " + post.getLikePost().size());
        User user = userRepository.findUserById(((CustomOidcUser) authentication.getPrincipal()).getUserId());
        if(post.getLikePost().contains(user)) post.getLikePost().remove(user);
        else post.getLikePost().add(user);
        Long like = Long.valueOf(post.getLikePost().size()) ;
        redisTemplate.opsForValue().set(KeyForRedis.getKeyForTotalPostLike(post.getId().toString()) , like );
        postRepository.save(post);
        recalculateScoreAfterUpdatePost(post);
        System.out.println("Like " + post.getLikePost().size());
        return true;
    }

    public Set<PostForSideBarDTO> getPostFollowingUser(Authentication authentication, HttpServletRequest request) {
        User user = userRepository.findUserById(((CustomOidcUser) authentication.getPrincipal()).getUserId());
        Set<User> followingUserSet = user.getFollowing();
        if (followingUserSet == null || followingUserSet.isEmpty()) {
            return Collections.emptySet();
        }
        List<Post> postList = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 1);
        for (User u : followingUserSet) {
            List<Post> posts = postRepository.findLatestPostByUser(u.getId(), pageable);
            if (!posts.isEmpty()) {
                postList.add(posts.get(0));
            }
        }
        if (postList.isEmpty()) {
            return Collections.emptySet();
        }
        Random random = new Random();
        Set<PostForSideBarDTO> postForSideBarDTOSet = new HashSet<>();
        while (postForSideBarDTOSet.size() < 5 && postForSideBarDTOSet.size() < postList.size()) {
            int r = random.nextInt(postList.size());
            Post post = postList.get(r);
            postForSideBarDTOSet.add(PostForSideBarDTO.toDTO(post));
        }
        return postForSideBarDTOSet;
    }


    public Set<PostSummaryDTO> getPostSummaryForSearch(String query , HttpServletRequest request){
        Set<PostSummaryDTO> postSummaryDTOSet;
        Integer page = 0;
        if(redisTemplate.hasKey(query + request.getSession().getId() + "post")){
            page = (Integer) redisTemplate.opsForValue().get(query + request.getSession().getId()+ "post");
        }
        System.out.println("page " + page);
        postSummaryDTOSet = postRepository.searchPostsByTitle(query ,PageRequest.of(page, 5)).stream().map(post -> {
            int like = post.getLikePost().size();
            int comment = post.getComments().size();
            return PostSummaryDTO.toDTO(post,Long.valueOf(like),Long.valueOf(comment));
        }).collect(Collectors.toSet());
        page++;
        if(postSummaryDTOSet.size() == 0){
            page = 0;
        }
        System.out.println("page " + page);
        redisTemplate.opsForValue().set(query + request.getSession().getId()+ "post", page);
        return postSummaryDTOSet;
    }

    public Set<PostSummaryDTO> getMyStory(Authentication authentication , HttpServletRequest request){
        Integer page = 0;
        if(redisTemplate.hasKey(request.getSession().getId() + "story")){
            page = (Integer) redisTemplate.opsForValue().get(request.getSession().getId() + "story");
        }
        Set<PostSummaryDTO> postSummaryDTOSet = postRepository.findLatestPostByUser(((CustomOidcUser) authentication.getPrincipal()).getUserId() ,PageRequest.of(page,5)).stream().map(post -> {
            Long likes = Long.valueOf(post.getLikePost().size());
            Long comments = Long.valueOf(post.getComments().size());
            return PostSummaryDTO.toDTO(post,likes,comments);

        }).collect(Collectors.toSet());
        page++;
        if(postSummaryDTOSet.size() == 0){
            page = 0;
        }
        redisTemplate.opsForValue().set(request.getSession().getId() + "story", page);
        return postSummaryDTOSet;
    }

    public void deletePostById(Long id){
        postRepository.deleteById(id);
    }

    public boolean editPost(PostRequestDTO postRequestDTO , Authentication authentication , HttpServletRequest request){
        Post post = postRepository.findById(postRequestDTO.getId()).orElse(null);
        if(post == null) return false;
        post.setTitle(postRequestDTO.getTitle());
        post.setContent(postRequestDTO.getContent());
        postRequestDTO.getCategories().forEach(System.out::println);
        Set<Category> categoriesSet = categoryRepository.findAllByName(postRequestDTO.getCategories());
        System.out.println("poss");
        post.getCategories().forEach(System.out::println);
        post.getCategories().addAll(categoriesSet);
        Set<Object> images = redisTemplate.opsForSet().members(KeyForRedis.getKeyForUploadImage(request.getSession().getId()));
        List<String> imagesSett = images.stream().map(imageUrl -> String.valueOf(imageUrl)).collect(Collectors.toList());
        post.getImages().addAll(imagesSett);
        postRepository.save(post);
        return true;
    }
}
