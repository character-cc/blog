package com.example.blog.config;

import com.example.blog.util.ImageUtil;
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
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ImageUtil imageUtil;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        String sessionId = event.getId();
        Set<String> imagesToDelete = redisTemplate.opsForSet().members(KeyForRedis.getKeyForUploadImage(sessionId));
//        imagesToDelete.forEach(image -> System.out.println(image));

        imageUtil.deleteImage(imagesToDelete);

    }

}
