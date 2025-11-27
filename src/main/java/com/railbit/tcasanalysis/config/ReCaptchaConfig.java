package com.railbit.tcasanalysis.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ReCaptchaConfig {
    @Value("${google.recaptcha.secret}")
    private String secret;
}
