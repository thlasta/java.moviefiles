package de.hlasta.moviefiles.moviefiles.persistence;

import de.hlasta.moviefiles.moviefiles.events.TmdbSearchEvent;
import de.hlasta.moviefiles.moviefiles.listener.TmdbSearchEventListener;
import de.hlasta.moviefiles.moviefiles.model.Movie;
import de.hlasta.moviefiles.moviefiles.publisher.CustomEventPublisher;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class TmdbEntity {

    private final File file;
    private TmdbMovies tmdbMovies;
    private TmdbSearch tmdbSearch;
    private String apiKey;
    private List<MovieDb> movieDb;
    static Logger logger = LoggerFactory.getLogger(TmdbEntity.class);

    @Autowired
    private CustomEventPublisher publisher;

    @Autowired
    private static TmdbSearchEventListener tmdbSearchEventListener;

    public TmdbEntity(File file) {
        this.file            = file;
        this.publisher       = new CustomEventPublisher();
        TmdbEntity.tmdbSearchEventListener = new TmdbSearchEventListener();
    }

    public TmdbEntity(String apiKey) {
        super();
        this.apiKey     = apiKey;
        this.file       = null;
        this.tmdbSearch = new TmdbSearch(new TmdbApi(apiKey));
        this.publisher  = new CustomEventPublisher();
        this.tmdbSearchEventListener = new TmdbSearchEventListener();
    }

    public TmdbEntity(String apiKey, File file) {
        super();
        this.apiKey     = apiKey;
        this.tmdbSearch = new TmdbSearch(new TmdbApi(apiKey));
        this.file       = file;
        this.publisher  = new CustomEventPublisher();
        tmdbSearchEventListener = new TmdbSearchEventListener();
    }

    public void searchMovie ( @NonNull String query,
                                       @NonNull Integer searchYear,
                                       @NonNull String language,
                                       boolean includeAdult,
                                       @NonNull Integer page ){

        tmdbSearchEventListener.onApplicationEvent(new TmdbSearchEvent<>(getFile(), TmdbSearchEventTypes.SEARCHING));

        movieDb = new ArrayList<>();

        MovieResultsPage foundMovie = getTmdbSearch()
                .searchMovie(query, searchYear, language, includeAdult, page);

        if (foundMovie != null) {
            movieDb = foundMovie.getResults();

            if (!movieDb.isEmpty()){
                Movie movie = new Movie();
                MovieDb bestFit = checkResults(movieDb.stream().collect(Collectors.toList()), query);

                tmdbSearchEventListener.onApplicationEvent(new TmdbSearchEvent<>(movie, bestFit, TmdbSearchEventTypes.FOUND));
            }
            else {
                tmdbSearchEventListener.onApplicationEvent(new TmdbSearchEvent<>(query, TmdbSearchEventTypes.NOTFOUND));
            }
        }
        else {
            tmdbSearchEventListener.onApplicationEvent(new TmdbSearchEvent<>(query, TmdbSearchEventTypes.NOTFOUND));
        }
    }

    public static void generateMovie(Movie movie, MovieDb movieDb) {

        movie.setId(UUID.randomUUID());
        movie.setTitle(movieDb.getTitle());
        movie.setGenres(movieDb.getGenres());
        movie.setReleaseDate(movieDb.getReleaseDate());
        movie.setReleaseYear(movie.getReleaseDate().substring(0,4));

        tmdbSearchEventListener.onApplicationEvent(new TmdbSearchEvent<>(movie, TmdbSearchEventTypes.FINISH));

    }

    private MovieDb checkResults(List<MovieDb> results, String queryName) {
        int ratio = 0;
        List<Tuple<Integer, MovieDb>> tmdbList = new ArrayList<>();

        String adjustedQueryName = MovieEntity.createQueryFileName(queryName, " ");

        if (results.size() > 0) {

            for (MovieDb result : results) {
                ratio = FuzzySearch.weightedRatio(result.getTitle(), adjustedQueryName);

                logger.info(String.format("--> [TmdbEntity.checkResults] : Weighted-Ratio %s -> %s (%s) : %s"
                        , ratio
                        , result.getTitle()
                        , result.getReleaseDate() == null || result.getReleaseDate().isEmpty() ? "0000" : result.getReleaseDate().substring(0,4)
                        , adjustedQueryName));

                // Tuple of ratio und result wird in eine Liste gespeichert.
                tmdbList.add(Tuple.of(ratio, result));

            }
            // Liste wird sortiert nach ratio.
            tmdbList.sort(Comparator.comparing((Tuple<Integer, MovieDb> i) -> i.ratio).reversed());

            // Der erste Eintrag der Liste wird dann verwendet. Sollte dann der Eintrag sein, mit der
            // höchsten Übereinstimmung.
            logger.info(String.format("--> [TmdbEntity.checkResults] : BestFit (ratio=%s) may be %s with ReleaseDate %s!"
                    , tmdbList.get(0).ratio
                    , tmdbList.get(0).movieDb.getTitle()
                    , tmdbList.get(0).movieDb.getReleaseDate()));

            return tmdbList.get(0).movieDb;

        }
        else
            logger.info(String.format("--> [TmdbEntity.checkResults] : Just %s Result found: %s ReleaseDate is %s "
                    , results.size()
                    , results.stream().findFirst().get()
                    , results.stream().findFirst().get().getReleaseDate().substring(0,4)));

        return results.stream().findFirst().get();
    }
}
