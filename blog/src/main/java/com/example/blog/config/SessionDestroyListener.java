package com.example.blog.config;

import com.example.blog.util.KeyForRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SessionDestroyListener implements ApplicationListener<SessionDestroyedEvent> {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {

        System.out.println("session destroyed");
        String sessionId = event.getId();
        Set<String > imagesToDelete = redisTemplate.opsForSet().members(KeyForRedis.getKeyForUploadImage(sessionId)).stream().map(image -> String.valueOf(image)).collect(Collectors.toSet());
        imagesToDelete.forEach(image -> System.out.println(image));
        for (String imageUrl : imagesToDelete) {
            String imagePath = imageUrl.replace("http://localhost/api/images/", System.getProperty("user.dir") + "/images/");
            File imageFile = new File(imagePath);
            System.out.println(imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }

    }

}
