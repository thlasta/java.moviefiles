package de.hlasta.moviefiles.moviefiles.ressource;

import de.hlasta.moviefiles.moviefiles.model.LocalFile;
import de.hlasta.moviefiles.moviefiles.model.Movie;
import de.hlasta.moviefiles.moviefiles.persistence.MovieEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/movie")
public class MovieRessource {

    MovieEntity movieEntity;

    @PostMapping("/all")
    public ResponseEntity<Movie> makeMovieFile(@RequestBody LocalFile localFile
                                             , @RequestParam boolean all) throws IOException {

        String path = localFile.rootPath();
        String extension = localFile.filterExtension();

        MovieEntity movieEntity = new MovieEntity();
        File f = new File(path);

        try{
            movieEntity.findAllFiles(f, extension).forEach(File -> {
                movieEntity.renameFile(File.getAbsoluteFile(), all);
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
