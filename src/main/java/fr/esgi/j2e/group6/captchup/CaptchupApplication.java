package fr.esgi.j2e.group6.captchup;

import fr.esgi.j2e.group6.captchup.user.repository.UserRepository;
import fr.esgi.j2e.group6.captchup.user.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CaptchupApplication {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean
    public UserService userService() {return new UserService();}*/

    public static void main(String[] args) {
        SpringApplication.run(CaptchupApplication.class, args);
    }

}