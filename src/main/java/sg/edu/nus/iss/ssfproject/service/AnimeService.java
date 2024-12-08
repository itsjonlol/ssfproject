package sg.edu.nus.iss.ssfproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.ssfproject.repo.AnimeRepo;

@Service
public class AnimeService {
    
    @Autowired
    AnimeRepo animeRepo;
}
