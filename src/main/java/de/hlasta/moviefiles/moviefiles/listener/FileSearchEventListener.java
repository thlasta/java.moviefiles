package de.hlasta.moviefiles.moviefiles.listener;

import de.hlasta.moviefiles.moviefiles.events.FileRenameEvent;
import de.hlasta.moviefiles.moviefiles.events.FileSearchEvent;
import de.hlasta.moviefiles.moviefiles.model.Movie;
import de.hlasta.moviefiles.moviefiles.persistence.FileRenameEventTypes;
import de.hlasta.moviefiles.moviefiles.persistence.FileSearchEventTypes;
import de.hlasta.moviefiles.moviefiles.persistence.MovieEntity;
import de.hlasta.moviefiles.moviefiles.persistence.TmdbEntity;
import info.movito.themoviedbapi.model.MovieDb;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.File;

@Getter
@Setter
public class FileSearchEventListener
        implements ApplicationListener<FileSearchEvent<Object>> {

    private File file;
    private FileSearchEventTypes types;

    public void onApplicationEvent(FileSearchEvent<Object> event) {
        Logger logger = LoggerFactory.getLogger(FileSearchEventListener.class);

        String logMessage = "--> [FileSearchEventListener.onApplicationEvent] : event : %s -> %s ";

        setTypes(event.getType() instanceof FileSearchEventTypes ? event.getType() : null);

        if (getTypes() != null){
            switch (getTypes()) {
                case FOUND, START, SEARCHING, FINISH -> {
                    setFile((event.getWhat() instanceof File) ? ((File) event.getWhat()) : null);

                    if (getFile() != null) {
                        logger.info(String.format(logMessage
                                , getTypes().toString()
                                , getFile().getName()));
                    }
                }

                default -> {
                    logger.info(String.format("--> [FileSearchEventListener.onApplicationEvent] : event : %s"
                            , getTypes().toString()));
                }
            }
        }
    }
}