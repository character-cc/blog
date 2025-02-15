package com.example.blog.service;

import com.example.blog.dto.*;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.*;
import com.example.blog.util.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
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

import org.hibernate.Session;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.EntityEntry;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private PostRepository postRepository;

    private RedisTemplate<String, String> redisTemplate;

    private CategoryRepository categoryRepository;

    private UserRepository userRepository;

    private ApplicationContext applicationContext;

    private UserContext userContext;

    private EntityManager entityManager;

    private ImageUtil imageUtil;

    private RedisService redisService;

    private CommentRepository commentRepository;

    private UnAuthenticatedUserContext unAuthenticatedUserContext;

    public PostService proxy() {
        return applicationContext.getBean(PostService.class);
    }


    public EntityNotFoundException postNotFoundException(Long postId) {
        return new EntityNotFoundException("Không tìm thấy bài viết với id: " + postId);
    }

    public void logEntityStates() {
        Session session = entityManager.unwrap(Session.class);
        SessionImplementor sessionImplementor = session.unwrap(SessionImplementor.class);
        PersistenceContext persistenceContext = sessionImplementor.getPersistenceContext();
        persistenceContext.managedEntitiesIterator().forEachRemaining(entity -> {
            EntityEntry entityEntry = persistenceContext.getEntry(entity);
            if (entityEntry != null) {
                String entityName = entityEntry.getPersister().getEntityName();
                Object entityId = entityEntry.getId();
                String status = entityEntry.getStatus().name();
                boolean isReadOnly = persistenceContext.isReadOnly(entity);
                System.out.println("Entity: " + entityName +
                        ", ID: " + entityId +
                        ", Status: " + status +
                        ", ReadOnly: " + isReadOnly);
            }
        });
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void recalculatePostScores() {
        redisTemplate.delete(KeyForRedis.getKeyForPostScore());
        List<Post> posts = postRepository.findPostByCreatedAtAfter(LocalDateTime.now().minusWeeks(1));
        Map<Long , Integer> totalLikesByPostIds = getTotalLikesByPostIds(posts.stream().map(Post::getId).collect(Collectors.toSet()));
        Map<Long , Integer> totalCommentsByPostIds = getTotalCommentsByPostIds(posts.stream().map(Post::getId).collect(Collectors.toSet()));
        for (Post post : posts) {
            long view = redisService.getTotalPostView(post.getId());
            int like = totalLikesByPostIds.getOrDefault(post.getId(), 0);
            int comment = totalCommentsByPostIds.getOrDefault(post.getId(), 0);
            redisService.setTotalPostComment(post.getId(), comment, 1, TimeUnit.DAYS);
            redisService.setTotalPostLike(post.getId(), like, 1, TimeUnit.DAYS);
            long day = ChronoUnit.DAYS.between(post.getCreatedAt(), LocalDateTime.now());
            long score = (view + like * 20 + comment * 200) * (long) Math.exp(day / 10.0);
            post.getCategories().forEach(category -> {
                redisService.addPostToCategory(category.getName(), post.getId(), 1, TimeUnit.DAYS);
            });
            redisService.addPostScore(post.getId(), score);
        }
    }

    public Set<PostSummaryDTO> getPostSummaryForCategories() {
//          category = category.replaceAll("\\s+", "");
        int start = 0;
        int end = 29;
        Set<Long> postIds = new HashSet<>();
        while(postIds.size() <= 15){
            List<Long> postIdsList = redisService.getTopPostIds(start, end).stream().toList();
            if(postIdsList.isEmpty()) break;
            for(Long postId : postIdsList){
                Boolean isMember;
                if(userContext.getUserId() != null){
                    isMember = redisService.isPostIdOfUser(postId, userContext.getUserId());
                    redisService.addViewedPostIdToUser(postId, userContext.getUserId());
                }
                else{
                    isMember = redisService.isPostIdOfUser(postId,unAuthenticatedUserContext.getIdentification());
                    redisService.addViewedPostIdToUser(postId, unAuthenticatedUserContext.getIdentification());
                }
                if(!isMember) postIds.add(postId);
                if(postIds.size() > 15) break;
            }
            start = end + 1;
            end = end + 29;
        }
        Set<PostSummaryDTO> postSummaryDTOSet = postRepository.findAllWithImagesAndAuthorByIdIn(postIds).stream().map(post -> {
            int like = redisService.getTotalPostLike(post.getId());
            int comment = redisService.getTotalPostComment(post.getId());
            return PostSummaryDTO.fromPost(post, like, comment);
        }).collect(Collectors.toSet());
        return postSummaryDTOSet;
    }


    public Long savePostAndRedis(updatePostDTO updatePostDTO, HttpServletRequest request) {
            Post post = proxy().savePost(updatePostDTO);
            if (post.getId() != null) {
                savePostRedis(post, request);
                return post.getId();
            }
        return null;
    }

    public void savePostRedis(Post post, HttpServletRequest request) {
        post.getImages().forEach(image -> redisTemplate.opsForSet().remove(
                KeyForRedis.getKeyForUploadImage(request.getSession().getId()), image));
        redisService.addUserIdToPostView(post.getId(), userContext.getUserId());
        redisService.setTotalPostComment(post.getId(), 0, 1, TimeUnit.DAYS);
        redisService.setTotalPostLike(post.getId(), 0, 1, TimeUnit.DAYS);
        post.getCategories().forEach(category -> {
            redisService.addPostToCategory(category.getName(), post.getId(), 1, TimeUnit.DAYS);
        });
        recalculateScoreAfterUpdatePost(post);
    }

    @Transactional
    public Post savePost(updatePostDTO updatePostDTO) {
        Post post = new Post();
        post.setTitle(updatePostDTO.getTitle());
        post.setContent(ExtractHtmlContent.convertRelativeToAbsoluteUrls(updatePostDTO.getContent()));
        Set<Category> categoriesSet = categoryRepository.findAllByName(updatePostDTO.getCategories()).stream().collect(Collectors.toSet());
        if(categoriesSet.isEmpty()) throw new EntityNotFoundException("Danh mục có thể đã bị xóa hoặc không tồn tại");
        post.setCategories(categoriesSet);
        User user = userRepository.getReferenceById(userContext.getUserId());
        post.setAuthor(user);
        Set<String> imagesSett = ExtractHtmlContent.extractImageUrls(post.getContent());
        post.setImages(imagesSett);
        postRepository.save(post);
        return post;
    }

    public void recalculateScoreAfterUpdatePost(Post post) {
        Integer likes = redisService.getTotalPostLike(post.getId());
        Integer comments = redisService.getTotalPostComment(post.getId());
        Long view =  redisService.getTotalPostView(post.getId());
        Long day = ChronoUnit.DAYS.between(post.getCreatedAt(), LocalDateTime.now());
        Long score = (view + likes * 20 + comments * 200) * (long) Math.exp(day / 10.0);
        redisService.addPostScore(post.getId(), score);
    }


    public PostDetailDTO getPostDetail(Long postId) {
        Post post = proxy().getPostByPostId(postId);
        int totalLikes = redisService.getTotalPostLike(post.getId());
        boolean isLikedByCurrentUser = false;
        if (userContext.getUserId() != null) {
            isLikedByCurrentUser = postRepository.isLiked(postId, userContext.getUserId()) > 0;
            redisService.addUserIdToPostView(post.getId(), userContext.getUserId());
            redisService.addViewedPostIdToUser(post.getId(), userContext.getUserId());
        } else {
            redisService.addViewedPostIdToUser(post.getId(), unAuthenticatedUserContext.getIdentification());
        }
        recalculateScoreAfterUpdatePost(post);
        return PostDetailDTO.fromPost(post, totalLikes, isLikedByCurrentUser);
    }

    @Transactional(readOnly = true)
    public Post getPostByPostId(Long postId) {
        Post post = postRepository.findWithImagesAndAuthorById(postId).orElseThrow(
                () -> postNotFoundException(postId)
        );
        postRepository.findWithCategoriesById(postId).orElseThrow(
                () -> postNotFoundException(postId)
        );
        logEntityStates();
        return post;
    }

    public void likePostAndRedis(Long postId, Authentication authentication) {
        Post post = proxy().likePost(postId);
        User user = new User();
        user.setId(userContext.getUserId());
        if (post.getLikes().contains(user)) {
            redisService.incrementTotalPostLike(postId, 1);
        } else {
            redisService.incrementTotalPostLike(postId, -1);
        }
        recalculateScoreAfterUpdatePost(post);
    }

    @Transactional
    public Post likePost(Long postId) {
        Post post = postRepository.findWithLikesById(postId).orElseThrow(
                () -> postNotFoundException(postId)
        );
        User user = userRepository.findById(userContext.getUserId()).orElse(null);
        if (post.getLikes().contains(user)) {
            post.getLikes().remove(user);
        } else {
            post.getLikes().add(user);
        }
        logEntityStates();
        return post;
    }


    Map<Long, Integer> getTotalLikesByPostIds(Set<Long> postIds) {
        return postRepository.getTotalLikesByPostIds(postIds)
                .stream().collect(Collectors.
                        toMap(row -> (Long) row[0], row -> ((Number) row[1]).intValue()));
    }

    Map<Long, Integer> getTotalCommentsByPostIds(Set<Long> postIds) {
        return postRepository.getTotalCommentsByPostIds(postIds)
                .stream().collect(Collectors.
                        toMap(row -> (Long) row[0], row -> ((Number) row[1]).intValue()));
    }

    @Transactional(readOnly = true)
    public List<PostSummaryDTO> getPostSummaryForSearch(String query, Integer page, Integer pageSize) {
        List<Post> posts = postRepository.searchPostsByTitle(query, PageRequest.of(page, pageSize));
        return getPostSummaryFromPosts(posts);
    }

    @Transactional(readOnly = true)
    public List<PostSummaryDTO> getPostSummaryFromPosts(List<Post> posts) {
        Set<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toSet());
        Map<Long, Integer> totalLikesByPostIds = getTotalLikesByPostIds(postIds);
        Map<Long, Integer> totalCommentsByPostIds = getTotalCommentsByPostIds(postIds);
        List<PostSummaryDTO> postSummaryDTOList = posts.stream().map(post -> {
            int likes = totalLikesByPostIds.getOrDefault(post.getId() , 0);
            int comments = totalCommentsByPostIds.getOrDefault(post.getId() , 0);
            return PostSummaryDTO.fromPost(post, likes, comments);
        }).collect(Collectors.toList());
        logEntityStates();
        return postSummaryDTOList;
    }

    @Transactional(readOnly = true)
    public List<PostSummaryDTO> getMyStory(Integer page) {
        List<Post> posts = postRepository.findLatestPostByUser(userContext.getUserId(), PageRequest.of(page, 10));
        return getPostSummaryFromPosts(posts);
    }

    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public Map<String, Object> updatePost(updatePostDTO updatePostDTO) {
        Post post = postRepository.findWithImagesById(updatePostDTO.getId()).orElseThrow(
                () -> postNotFoundException(updatePostDTO.getId()));
        postRepository.findWithCategoriesById(updatePostDTO.getId()).orElseThrow(
                () -> postNotFoundException(updatePostDTO.getId()));
        post.setTitle(updatePostDTO.getTitle());
        post.setContent(ExtractHtmlContent.convertRelativeToAbsoluteUrls(updatePostDTO.getContent()));

        Set<Category> newCategories = categoryRepository.findAllByName(updatePostDTO.getCategories()).stream().collect(Collectors.toSet());
        if (newCategories.isEmpty()) throw new EntityNotFoundException("Các danh mục bạn đã chọn đã bị xóa hoặc không tồn tại");
        Set<Category> currentCategories = new HashSet<>(post.getCategories());
        Set<Category> categoriesToRemove = currentCategories.stream()
                .filter(category -> !newCategories.contains(category))
                .collect(Collectors.toSet());
        post.getCategories().removeAll(categoriesToRemove);
        post.getCategories().addAll(newCategories);


        Set<String> newImages = ExtractHtmlContent.extractImageUrls(post.getContent());
        Set<String> currentImages = new HashSet<>(post.getImages());
        Set<String> imagesToRemove = currentImages.stream()
                .filter(image -> !newImages.contains(image))
                .collect(Collectors.toSet());
        Set<String> imagesToAdd = newImages.stream()
                .filter(image -> !currentImages.contains(image))
                .collect(Collectors.toSet());
        post.getImages().removeAll(imagesToRemove);
        post.getImages().addAll(imagesToAdd);


        this.logEntityStates();

        Map<String, Object> map = new HashMap<>();
        map.put("imagesToAdd", imagesToAdd);
        map.put("post", post);
        map.put("imagesToRemove", imagesToRemove);
        return map;
    }


    public Long editPost(updatePostDTO updatePostDTO , HttpServletRequest request) {
        Map<String, Object> m = proxy().updatePost(updatePostDTO);

//        Lí do không thể xóa trong Transaction là do nếu xảy ra rollback thì
//        những ảnh của content cũ sẽ không còn trong thư mục image

        Object imagesToAddObj = m.get("imagesToAdd");
        if (imagesToAddObj instanceof Set<?>) {
            Set<String> imagesToAdd = (Set<String>) imagesToAddObj;
            imagesToAdd.forEach(image -> redisTemplate.opsForSet().remove(
                    KeyForRedis.getKeyForUploadImage(request.getSession().getId()), image
            ));
        }
        Object imagesToRemoveObj = m.get("imagesToRemove");
        if (imagesToRemoveObj instanceof Set<?>) {
            Set<String> imagesToRemove = (Set<String>) imagesToRemoveObj;
            if (!imagesToRemove.isEmpty()) {
                imageUtil.deleteImage(imagesToRemove);
            }
        }
        Object postObj = m.get("post");
        return ((Post) postObj).getId();
    }
}
