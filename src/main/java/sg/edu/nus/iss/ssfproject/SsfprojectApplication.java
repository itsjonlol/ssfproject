package sg.edu.nus.iss.ssfproject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sg.edu.nus.iss.ssfproject.constant.ConstantVar;
import sg.edu.nus.iss.ssfproject.service.AnimeService;

@SpringBootApplication
public class SsfprojectApplication implements CommandLineRunner{

	@Autowired
	AnimeService animeService;

	public static void main(String[] args) {
		SpringApplication.run(SsfprojectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//instantiate once
		if (!animeService.hasRedisKey(ConstantVar.genresRedisKey)) {
			animeService.getAnimeGenre();
			List<String> animeGenresList = animeService.getAnimeGenres();
			//so can pre-load the first category
			animeService.getAnimeListByGenre(animeGenresList.get(0));
			// for (String genre : animeGenresList) {
			// 	animeService.getAnimeListByGenre(genre);
			// }
		}
		//maybe can load all the categories into redis first.
		
		

		
	}



}
