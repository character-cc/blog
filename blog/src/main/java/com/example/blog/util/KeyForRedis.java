package com.example.blog.util;

public class KeyForRedis {

    public static String getKeyForPostView(String postId) {
        return "post_view_" + postId;
    }

    public static String getKeyForTotalPostComment(String postId) {
        return "total_post_comment_" + postId;
    }

    public static String getKeyForTotalPostLike(String postId) {
        return "total_post_like_" + postId;
    }

    public static String getKeyForPostScore() {
        return "post_score";
    }

    public static String getKeyForCategory(String name) {
        return "category_" + name;
    }

    public static String getKeyForUploadImage(String sessionId) {
        return "upload_image_" + sessionId;
    }

    public static String getKeyForPositionPost(String sessionId , String category) { return "position_post_" + sessionId + "_" + category; }

    public static String getKeyForFavoriteCategory(String category , String sessionId) { return "favorite_category_" + category + sessionId; }
}
