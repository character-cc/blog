package com.example.blog.util;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Set;

@Component
public class ImageUtil {

    public void deleteImage(Set<String> imagePaths) {
        for (String imageUrl : imagePaths) {
            String imagePath = imageUrl.replace("http://localhost/api/images/", System.getProperty("user.dir") + "/images/");
            deleteImage(imagePath);
        }
    }

    public void deleteImage(String path) {
        File imageFile = new File(path);
        System.out.println(path);
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }
}
