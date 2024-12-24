package sg.edu.nus.iss.ssfproject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import sg.edu.nus.iss.ssfproject.service.AnimeService;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SsfprojectApplication implements CommandLineRunner{

	@Autowired
	AnimeService animeService;

	public static void main(String[] args) {
		SpringApplication.run(SsfprojectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		//api error handled in service
		animeService.getAnimeGenresFromApiCall();
		List<String> animeGenresList = animeService.getAnimeGenresForFrontPage();
		//so can pre-load the first category
		animeService.getAnimeListByGenre(animeGenresList.get(0));
		
		

		
	}



}
