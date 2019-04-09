package com.hk.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author smallHK
 * 2019/4/7 23:29
 */
@Configuration
public class MailConfiguration {

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.qq.com");
        mailSender.setUsername("xiaoheiwanghk@qq.com");
        mailSender.setPassword("jucyyigyllcfdjee");
        return mailSender;
    }
}
