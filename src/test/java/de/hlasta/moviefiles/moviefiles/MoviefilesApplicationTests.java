package de.hlasta.moviefiles.moviefiles;

import de.hlasta.moviefiles.moviefiles.persistence.MovieEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
class MoviefilesApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void makeAllFilesToMovieFiles() throws IOException {
//        String path = "f:/International";
		String path = "q:/Deutsch";
//		String path = "c:/Users/thlasta/Videos/";
//		String extension = ".mkv";
		String extension = ".mp4";

		boolean all = false;

		MovieEntity movieEntity = new MovieEntity(all);

		File file = new File(path);

		try{
			movieEntity.findAllFiles(file, extension).forEach(File -> {
				movieEntity.renameFile(File.getAbsoluteFile(), all);
			});
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
