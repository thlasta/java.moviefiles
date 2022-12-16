package de.hlasta.moviefiles.moviefiles.persistence;

import de.hlasta.moviefiles.moviefiles.events.FileRenameEvent;
import de.hlasta.moviefiles.moviefiles.events.FileSearchEvent;
import de.hlasta.moviefiles.moviefiles.events.TmdbSearchEvent;
import de.hlasta.moviefiles.moviefiles.listener.FileRenameEventListener;
import de.hlasta.moviefiles.moviefiles.listener.FileSearchEventListener;
import de.hlasta.moviefiles.moviefiles.listener.TmdbSearchEventListener;
import de.hlasta.moviefiles.moviefiles.model.Movie;
import de.hlasta.moviefiles.moviefiles.publisher.CustomEventPublisher;
import info.movito.themoviedbapi.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class MovieEntity {

    private UUID id;
    private String oldFileName;
    private String newFileName;
    private String extension;
    private String queryFileName;
    private String releaseYear;
    private List <Movie> movies;
    private TmdbMovies movie;
    private TmdbSearch results;
    private TmdbEntity tmdbSearch;

    private boolean checkAllFiles;

    private static String apiKey = "eea03afb3d4f1e8f88b17ee08c7af508";

    static Logger logger = LoggerFactory.getLogger(MovieEntity.class);

    @Autowired
    private CustomEventPublisher publisher;

    @Autowired
    private TmdbSearchEventListener tmdbListener;

    @Autowired
    private static FileRenameEventListener fileRenameListener;

    @Autowired
    private static FileSearchEventListener fileSearchListener;


    public MovieEntity() {
        movie              = new TmdbApi(apiKey).getMovies();
        results            = new TmdbSearch(new TmdbApi(apiKey));
        tmdbSearch         = new TmdbEntity(apiKey);

        publisher          = new CustomEventPublisher();
        tmdbListener       = new TmdbSearchEventListener();
        fileRenameListener = new FileRenameEventListener();
        fileSearchListener = new FileSearchEventListener();

        checkAllFiles      = false;
    }

    public MovieEntity(boolean all) {

        movie              = new TmdbApi(apiKey).getMovies();
        results            = new TmdbSearch(new TmdbApi(apiKey));
        tmdbSearch         = new TmdbEntity(apiKey);

        publisher          = new CustomEventPublisher();
        tmdbListener       = new TmdbSearchEventListener();
        fileRenameListener = new FileRenameEventListener();
        fileSearchListener = new FileSearchEventListener();

        checkAllFiles      = all;
    }

    public void renameFile(File file, boolean all) {

        setNewFileName(createNewFileName(file.getName()));

        if (all || !isReleaseYearInFileName(getNewFileName())) {
            String qName = createQueryFileName(getNewFileName(), "+");
            logger.info(String.format("--> [MovieEntity:renameFile] : MovieTmdb QueryName : %s", qName));

            try {
                tmdbListener.onApplicationEvent(new TmdbSearchEvent<>(file, TmdbSearchEventTypes.START));

                new TmdbEntity(apiKey, file).searchMovie(qName, 0, "de-DE", true, 1);

            }
            catch (Exception ex) {
                fileRenameListener.onApplicationEvent(new FileRenameEvent<Object>(file
                        , newFileName
                        , ex.getMessage()
                        , FileRenameEventTypes.FAILURE));
            }
        }
    }

    public static void renameFile(File file, Movie movie) {

        fileRenameListener.onApplicationEvent(new FileRenameEvent<>(file, movie, FileRenameEventTypes.START));

        Boolean isRenamed = false;

        String newFileName = file.getName();

        if (file != null && movie != null) {
            fileRenameListener.onApplicationEvent(new FileRenameEvent<>(file, movie, FileRenameEventTypes.RENAMING));

            newFileName = MovieEntity.createNewFileName(file.getName());

            newFileName = MovieEntity.addReleaseYearIfNessessary(newFileName, movie.getReleaseYear());

            try {

                isRenamed = file.getAbsoluteFile().renameTo(new File(file.getAbsolutePath().replace(file.getName(), newFileName)));

                if (isRenamed)
                    fileRenameListener.onApplicationEvent(new FileRenameEvent<>(file
                                                        , newFileName
                                                        , FileRenameEventTypes.RENAMED));
                else
                    fileRenameListener.onApplicationEvent(new FileRenameEvent<>(file
                                                        , newFileName
                                                        , new String("renameTo:false")
                                                        , FileRenameEventTypes.FAILURE));

            }
            catch (Exception ex){

                fileRenameListener.onApplicationEvent(new FileRenameEvent<Object>(file
                                                                          , newFileName
                                                                          , ex.getMessage()
                                                                          , FileRenameEventTypes.FAILURE));

            }
        }

        fileRenameListener.onApplicationEvent(new FileRenameEvent<>(file, newFileName, FileRenameEventTypes.FINISH));
    }

    private static boolean isReleaseYearInFileName(String fileName) {
        boolean rename = false;

        Pattern pattern = Pattern.compile("(.[0-9]{4}).");
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()){
            logger.debug(String.format("--> [MovieEntity:isReleaseYearInFileName] : Looks like there is a ReleaseDate in Filename : %s"
                    , fileName));
            rename = true;
        }

        return  rename;
    }

    private static String addReleaseYearIfNessessary(String fileName, String releaseYear) {
        String newFileNameWithReleaseYear = fileName;

        String extension = fileName.substring(fileName.lastIndexOf('.'), fileName.length());

        if (!isReleaseYearInFileName(fileName)){
            newFileNameWithReleaseYear = fileName.replace(extension,
                                        new String(String.format(".(%s)", releaseYear).concat(extension)));

            logger.info(String.format("--> [MovieEntity:addReleaseYearIfNessessary] : ReleaseDate %s added : %s"
                    , releaseYear
                    , newFileNameWithReleaseYear));

        }
        else {
            Pattern pattern = Pattern.compile("(.[0-9]{4}).");
            Matcher matcher = pattern.matcher(fileName);

            String orgExtension = fileName.substring((fileName.length() - extension.length() - 5)
                                                    ,(fileName.length() - extension.length() - 1));

            if (!orgExtension.equals(releaseYear)) {
                newFileNameWithReleaseYear = matcher.replaceAll("").replace(extension,
                        new String(String.format("(%s)", releaseYear).concat(extension)));

                logger.info(String.format("--> [MovieEntity:addReleaseYearIfNessessary] : ReleaseDate %s replaced : %s"
                        , releaseYear
                        , newFileNameWithReleaseYear));
            }
        }

        return newFileNameWithReleaseYear;
    }

    public static String createQueryFileName(String name, String trenner) {

        String newName = name;

        try {
            // Dateiendung rausschneiden
            newName = name.substring(0, name.lastIndexOf('.'));

            // Releasejahr bsp. ".(2020)" rausschneiden
            newName = newName.substring(0, name.lastIndexOf(".("));
        }
        catch (IndexOutOfBoundsException ex) {
            logger.warn(String.format("--> [MovieEntity:createQueryFileName] : File extension or ReleaseYear in %s not present!", newName));
        }
        finally {
            // Punkte im Dateinamen werden durch "trenner" ersetzt.
            newName = newName.replaceAll("\\.", trenner);
            logger.debug(String.format("--> [MovieEntity:createQueryFileName] : Filename %s (with %s)", name, trenner));

            return newName;
        }
    }

    private static String createNewFileName(String orgFile) {

        if (orgFile.contains(" ") ||
            orgFile.contains("-") ||
            orgFile.contains("_")) {

            logger.info(String.format("--> [MovieEntity:createNewFileName] : File will be changed : %s ", orgFile));

            for (String s : Arrays.asList(" ", "\\-", "\\_", "\\.\\.\\.", "\\.\\."))
                orgFile = orgFile.replaceAll(s, ".");

            String[] split = orgFile.split("\\.");

            // Das letzte Item im Array sollte die Dateiendung sein
            // und darf natürlich nicht umbenannt werden!
            for (int i = 0; i < Arrays.stream(split).count() - 1; i++) {
                String word = split[i];
                if (!word.isEmpty()) {
                    orgFile = orgFile.replace(word, toUpperCaseFirst(word));
                }
            }
        }
        else
            logger.debug(String.format("--> [MovieEntity:createNewFileName] : File does not need to be changed : %s ", orgFile));

        return orgFile;
    }

    private static String toUpperCaseFirst(String name) {
        char[] arr = name.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }

    public ArrayList<File> findAllFiles(File dir, String extension) throws IOException {

        fileSearchListener.onApplicationEvent(new FileSearchEvent<>(dir, FileSearchEventTypes.START));
        setExtension(extension);
        File[] files = dir.listFiles();
        ArrayList<File> matches = new ArrayList<> ();

        if (files != null) {
            fileSearchListener.onApplicationEvent(new FileSearchEvent<>(dir, FileSearchEventTypes.SEARCHING));
            try{
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().toLowerCase(Locale.ROOT).endsWith(extension)) {
                        fileSearchListener.onApplicationEvent(new FileSearchEvent<>(files[i], FileSearchEventTypes.FOUND));
                        matches.add(files[i]);

                    }
                    if (files[i].isDirectory()) {
                        matches.addAll(findAllFiles(files[i], extension));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                fileSearchListener.onApplicationEvent(new FileSearchEvent<>(dir, FileSearchEventTypes.FINISH));
                return matches;
            }
        }
        return matches;
    }

}
