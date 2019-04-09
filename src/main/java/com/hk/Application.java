package com.hk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;

/**
 * smallHK
 * 2019/3/20 21:52
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication application = new SpringApplication(Application.class);

//        for(ApplicationContextInitializer<?> initializer: application.getInitializers()) {
//            System.out.println(initializer.getClass().getName());
//        }
//
//        for (ApplicationListener<?> listener: application.getListeners()) {
//            System.out.println(listener.getClass().getName());
//        }
        application.run(args);

    }
}
