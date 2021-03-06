package fr.esgi.j2e.group6.captchup.level.web;

import fr.esgi.j2e.group6.captchup.level.model.Level;
import fr.esgi.j2e.group6.captchup.level.model.LevelAnswer;
import fr.esgi.j2e.group6.captchup.level.model.Prediction;
import fr.esgi.j2e.group6.captchup.level.repository.LevelAnswerRepository;
import fr.esgi.j2e.group6.captchup.level.repository.LevelRepository;
import fr.esgi.j2e.group6.captchup.level.repository.PredictionRepository;
import fr.esgi.j2e.group6.captchup.level.service.LevelAnswerService;
import fr.esgi.j2e.group6.captchup.level.service.LevelService;
import fr.esgi.j2e.group6.captchup.level.service.PredictionService;
import fr.esgi.j2e.group6.captchup.user.model.User;
import fr.esgi.j2e.group6.captchup.user.service.UserService;
import fr.esgi.j2e.group6.captchup.vision.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/level")
public class LevelController {

    @Autowired LevelRepository levelRepository;
    @Autowired LevelService levelService;
    @Autowired UserService userService;
    @Autowired VisionService visionService;
    @Autowired PredictionService predictionService;
    @Autowired LevelAnswerRepository levelAnswerRepository;
    @Autowired LevelAnswerService levelAnswerService;
    @Autowired PredictionRepository predictionRepository;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/all")
    public Iterable<Level> getAllLevels() {
        return levelRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Level> getLevelById(@PathVariable("id") int id) {
        Optional<Level> level = levelRepository.findById(id);

        if(!level.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(level.get());
    }

    @PostMapping(path = "/create")
    public ResponseEntity<Level> createLevel(@RequestBody MultipartFile image) {
        User user = userService.getCurrentLoggedInUser();

        if(visionService.maxAmountOfCallsIsReached(LocalDate.now(), user)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }

        try {
            Level level = levelService.createLevel(image, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(level);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(path = "/{id}/solve")
    public ResponseEntity<LevelAnswer> solveLevel(@PathVariable("id") Integer id, @RequestParam("answer") String answer) {
        LevelAnswer levelAnswer;

        try {
            levelAnswer = levelService.solveLevel(id, answer);
        }catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(levelAnswer);
    }

    @GetMapping(path = "/{id}/predictions/solved")
    public Iterable<Prediction> getSolvedPredictions(@PathVariable("id") int levelId) {
        User user = userService.getCurrentLoggedInUser();

        return predictionRepository.findSolvedPredictions(levelId, user.getId());
    }

    @GetMapping(path = "/explore")
    public Iterable<Level> exploreLevels() {
        User user = userService.getCurrentLoggedInUser();
        return levelRepository.findAllUntestedLevels(user.getId());
    }
}
