package de.hlasta.moviefiles.moviefiles.listener;

import de.hlasta.moviefiles.moviefiles.events.FileRenameEvent;
import de.hlasta.moviefiles.moviefiles.events.TmdbSearchEvent;
import de.hlasta.moviefiles.moviefiles.model.Movie;
import de.hlasta.moviefiles.moviefiles.persistence.FileRenameEventTypes;
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
public class FileRenameEventListener
        implements ApplicationListener<FileRenameEvent<Object>> {

    private File file;
    private Movie movie;
    private MovieDb movieDb;
    private String newFileName;
    private String errorMessage;
    private FileRenameEventTypes types;

    public void onApplicationEvent(FileRenameEvent<Object> event) {
        Logger logger = LoggerFactory.getLogger(FileRenameEventListener.class);

        String logMessage = "--> [FileRenameEventListener.onApplicationEvent] : event : %s -> %s ";

        setTypes(event.getType() instanceof FileRenameEventTypes ? ((FileRenameEventTypes) event.getType()) : null);

        if (getTypes() != null){
            switch (getTypes()) {
                case FAILURE -> {

                    setFile((event.getWhereto() instanceof File) ? ((File) event.getWhereto()) : null);
                    setNewFileName((event.getWhat() instanceof String) ? (event.getWhat().toString()) : null);
                    setErrorMessage((event.getErrorMessage() instanceof String) ? event.getErrorMessage() : new String("???"));

                    if (getFile() != null && getNewFileName() != null && getErrorMessage() != null) {
                        logger.info(String.format("--> [FileRenameEventListener.onApplicationEvent] : event : %s -> %s to %s: %s"
                                , getTypes().toString()
                                , getFile().getName()
                                , getNewFileName()
                                , getErrorMessage()));
                    }
                }
                case RENAMED -> {
                    setFile((event.getWhereto() instanceof File) ? ((File) event.getWhereto()) : null);
                    setNewFileName((event.getWhat() instanceof String) ? (event.getWhat().toString()) : null);

                    if (getFile() != null && getNewFileName() != null) {
                        logger.info(String.format(logMessage
                                , getTypes().toString()
                                , getFile().getName().concat(" to ")
                                                     .concat(getNewFileName())));
                    }
                }
                case FINISH -> {
                    setFile(event.getWhereto() instanceof File ? ((File) event.getWhereto()) : null);
                    setNewFileName((event.getWhat() instanceof String) ? (event.getWhat().toString()) : null);

                    if (getFile() != null && getNewFileName() != null) {
                        logger.info(String.format(logMessage
                                , getTypes().toString()
                                , getMovie().getTitle()));

                    }
                }
                case START, RENAMING -> {
                    setFile((event.getWhereto() instanceof File) ? ((File) event.getWhereto()) : null);
                    setMovie((event.getWhat() instanceof Movie) ? ((Movie) event.getWhat()) : null);

                    if (getFile() != null && getMovie() != null) {
                        logger.info(String.format(logMessage
                                , getTypes().toString()
                                , getFile().getName()));

                    }
                }

                default -> {
                    logger.info(String.format(logMessage
                            , getTypes().toString()));
                }
            }
        }
    }
}