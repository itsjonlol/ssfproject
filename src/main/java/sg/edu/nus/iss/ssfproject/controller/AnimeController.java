package sg.edu.nus.iss.ssfproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import sg.edu.nus.iss.ssfproject.service.AnimeService;

@Controller
public class AnimeController {
    
    @Autowired
    AnimeService animeService;
    
}
