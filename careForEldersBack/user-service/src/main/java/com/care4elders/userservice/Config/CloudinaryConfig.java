package com.care4elders.userservice.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dqmn4ssji",
                "api_key", "457756782479947",
                "api_secret", "kQ5krWMRh5OKySk3Klz8Idwd9RA",
                "secure", true
        ));
    }
}
