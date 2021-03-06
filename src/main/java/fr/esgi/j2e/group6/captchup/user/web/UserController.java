package fr.esgi.j2e.group6.captchup.user.web;

import fr.esgi.j2e.group6.captchup.level.model.Level;
import fr.esgi.j2e.group6.captchup.level.service.LevelService;
import fr.esgi.j2e.group6.captchup.user.model.User;
import fr.esgi.j2e.group6.captchup.user.repository.UserRepository;
import fr.esgi.j2e.group6.captchup.user.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@RestController
@RequestMapping(path="/user")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired private UserService userService;
    @Autowired private LevelService levelService;

    public UserController() { }

    @GetMapping(path="/all")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int id) {
        Optional<User> user = userRepository.findById(id);

        if(!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(user.get());
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") int id) {
        try {
            userService.deleteUser(id);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PostMapping(path="/sign-up")
    public ResponseEntity<User> signUp(@RequestBody User user) {
        User foundUser = userRepository.findByUsername(user.getUsername());
        if(foundUser == null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(user));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PatchMapping(path = "/follow/{id}")
    public ResponseEntity<User> followUser(@PathVariable("id") int id) {
        User user = null;
        try {
            user = userService.followUser(userRepository.findById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PatchMapping(path = "/unfollow/{id}")
    public ResponseEntity<User> unfollowUser(@PathVariable("id") int id) {
        User user;
        try {
            user = userService.unfollowUser(userService.getCurrentLoggedInUser(), userRepository.findById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping(path = "/{id}/level/finished")
    public Iterable<Level> getFinishedLevelsBy(@PathVariable("id") int id) {
        return levelService.getFinishedLevels(id);
    }

    @GetMapping(path = "/{id}/level/unfinished")
    public Iterable<Level> getUnfinishedLevelsBy(@PathVariable("id") int id) {
        return levelService.getUnfinishedLevels(id);
    }
}
