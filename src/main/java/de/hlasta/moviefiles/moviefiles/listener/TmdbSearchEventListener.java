package de.hlasta.moviefiles.moviefiles.listener;

import de.hlasta.moviefiles.moviefiles.events.TmdbSearchEvent;
import de.hlasta.moviefiles.moviefiles.model.Movie;
import de.hlasta.moviefiles.moviefiles.persistence.MovieEntity;
import de.hlasta.moviefiles.moviefiles.persistence.TmdbEntity;
import de.hlasta.moviefiles.moviefiles.persistence.TmdbSearchEventTypes;
import info.movito.themoviedbapi.model.MovieDb;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.File;

@Getter
@Setter
public class TmdbSearchEventListener
        implements ApplicationListener<TmdbSearchEvent<Object>> {

    private File file;
    private Movie movie;
    private MovieDb movieDb;
    private String text;
    private TmdbSearchEventTypes types;

    public void onApplicationEvent(TmdbSearchEvent<Object> event) {
        Logger logger = LoggerFactory.getLogger(TmdbSearchEventListener.class);

        String logMessage = "--> [TmdbSearchEventListener.onApplicationEvent] : event : %s -> %s ";

        setTypes(event.getType() instanceof TmdbSearchEventTypes ? ((TmdbSearchEventTypes) event.getType()) : null);

        if (getTypes() != null){
            switch (getTypes()) {
                case FOUND -> {
                    setMovieDb(event.getWhat() instanceof MovieDb ? ((MovieDb) event.getWhat()) : null);
                    setMovie(event.getWhereto() instanceof Movie ? ((Movie) event.getWhereto()) : null);

                    if (getMovieDb() != null && getMovie() != null) {
                        logger.info(String.format(logMessage
                                , getTypes().toString()
                                , getMovieDb().getTitle()));

                        TmdbEntity.generateMovie(getMovie(), getMovieDb());
                    }
                }
                case FINISH -> {
                    setMovie(event.getWhat() instanceof Movie ? ((Movie) event.getWhat()) : null);

                    if (getFile() != null && getMovie() != null) {
                        logger.info(String.format(logMessage
                                , getTypes().toString()
                                , getMovie().getTitle()));

                        MovieEntity.renameFile(getFile(), getMovie());
                    }
                }
                case START, SEARCHING -> {
                    setFile((event.getWhat() instanceof File) ? ((File) event.getWhat()) : null);

                    if (getFile() != null)
                        logger.info(String.format(logMessage
                                , getTypes().toString()
                                , getFile().getName()));
                }
                case NOTFOUND -> {
                    setText((event.getWhat() instanceof String) ? event.getWhat().toString() : new String ("???"));

                    if (getText() != null)
                        logger.info(String.format(logMessage
                                , getTypes().toString()
                                , getFile().getName()));
                }

                default -> {
                    logger.info(String.format(logMessage
                            , getTypes().toString()));
                }
            }
        }
    }
}