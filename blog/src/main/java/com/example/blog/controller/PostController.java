package com.example.blog.controller;

import com.example.blog.dto.PostRequestDTO;
import com.example.blog.service.PostService;
import com.example.blog.util.ApiResponse;
import com.example.blog.util.KeyForRedis;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class PostController {

//    @GetMapping(value = "/posts/foryou" , produces = "application/json")
//    public ResponseEntity<ApiResponse<?>> postsForYou(Authentication auth) {
//        if(auth == null || !auth.isAuthenticated()) {
//
//        }
//
//    }
      private PostService postService;

      private RedisTemplate redisTemplate;

@PostMapping("/posts/images/upload")
public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file , HttpServletRequest request) {
    try {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String uploadDir = System.getProperty("user.dir") + "//images/";
        System.out.println(uploadDir);
        File uploadFile = new File(uploadDir + fileName);
        uploadFile.getParentFile().mkdirs();
        file.transferTo(uploadFile);
        String imageUrl = "http://localhost/api/images/" + fileName;
//            ApiResponse<String> apiResponse = ApiResponse.success(imageUrl,"Yafnhha");

        Map<String, String> response = new HashMap<>();
        response.put("location", imageUrl);
        redisTemplate.opsForSet().add(KeyForRedis.getKeyForUploadImage(request.getSession().getId()), imageUrl);
        return ResponseEntity.ok().body(response);
    } catch (IOException e) {
        System.out.println(e.getMessage());
//            ApiResponse<String> apiResponse = ApiResponse.success(e.getMessage(),"Yafnhha");
        Map<String, String> response = new HashMap<>();
        response.put("locatsfdsion", "jjd");
        return ResponseEntity.badRequest().body(response);
    }
}


    @PostMapping(value = "/posts/upload")
    public ResponseEntity<?> createPost(@RequestBody PostRequestDTO postRequestDTO , Authentication authentication , HttpServletRequest request) {
//        System.out.println("Title: " + postRequest.getTitle());
//        System.out.println("Categories: " + postRequest.getCategories());
//        System.out.println("Content: " + postRequest.getContent());
        try {
            boolean success = postService.savePost(postRequestDTO, authentication, request);
            if (success) {
                return ResponseEntity.ok("Thành công");
            }
            return ResponseEntity.badRequest().body("Thất bạ");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            // Đường dẫn tới thư mục chứa ảnh
            Path path = Paths.get("images/" + fileName);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Lấy phần mở rộng của file để xác định loại ảnh
                String contentType = getContentType(fileName);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))  // Set Content-Type
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).body(null);  // Trả về lỗi nếu ảnh không tìm thấy
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(500).body(null);  // Trả về lỗi nếu không đọc được ảnh
        }
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (fileName.endsWith(".webp")) {
            return "image/webp";
        } else {
            return "application/octet-stream";
        }
    }

}
