package com.example.blog.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

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
        // Xác định kiểu ảnh từ phần mở rộng file
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
            return "application/octet-stream";  // Nếu không nhận diện được định dạng
        }
    }

}
