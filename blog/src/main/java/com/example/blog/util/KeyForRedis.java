package com.example.blog.util;

public class KeyForRedis {

    public static String getKeyForPostView(Long postId) {
        return "post_view_" + postId;
    }

    public static String getKeyForTotalPostComment(Long postId) {
        return "total_post_comment_" + postId;
    }

    public static String getKeyForTotalPostLike(Long postId) {
        return "total_post_like_" + postId;
    }

    public static String getKeyForPostScore() {
        return "post_score";
    }

    public static String getKeyForCategory(String name) {
        return "category_" + name.replaceAll("\\s+", "");
    }

    public static String getKeyForUploadImage(String sessionId) {
        return "upload_image_" + sessionId;
    }

    public static String getKeyForViewedPostToUser(Long userId) {return "post_to_user_" + userId;}

    public static String getKeyForViewedPostToUnAuthenticatedUser(String identification) {return "post_to_unauthenticated_user_" + identification;}



}
