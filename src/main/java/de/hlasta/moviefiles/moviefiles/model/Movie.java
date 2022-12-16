package de.hlasta.moviefiles.moviefiles.model;


import info.movito.themoviedbapi.model.Genre;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Movie {

    UUID id;
    Integer ratio;
    String title;
    String releaseYear;
    String rating;
    String releaseDate;
    Integer runtime;
    List <Genre> genres;
    List<Person> directors;
    List <Person> writers;
    List <Person> actors;
    String plotSummary;
    List <String> languages;
    List <String> countries;
    List <String> awardsWon;
    List <URI> moviePostersUrl;
    List <String> ratingsReceived;
    String metascore;
    String iMDbRating;
    List <String> iMDbVotes;
    String iMDbID;
    MovieTypes type;
    String infoDVD;
    List <String> boxOfficeResults;
    String productionCompangne;
    List <URI> websites;

    public Movie(UUID id,
                 String title,
                 String releaseYear,
                 String rating,
                 String releaseDate,
                 Integer runtime,
                 List<Genre> genres,
                 List<Person> directors,
                 List<Person> writers,
                 List<Person> actors,
                 String plotSummary,
                 List<String> languages,
                 List<String> countries,
                 List<String> awardsWon,
                 List<URI> moviePostersUrl,
                 List<String> ratingsReceived,
                 String metascore,
                 String iMDbRating,
                 List<String> iMDbVotes,
                 String iMDbID,
                 MovieTypes type,
                 String infoDVD,
                 List<String> boxOfficeResults,
                 String productionCompangne,
                 List<URI> websites) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.genres = genres;
        this.directors = directors;
        this.writers = writers;
        this.actors = actors;
        this.plotSummary = plotSummary;
        this.languages = languages;
        this.countries = countries;
        this.awardsWon = awardsWon;
        this.moviePostersUrl = moviePostersUrl;
        this.ratingsReceived = ratingsReceived;
        this.metascore = metascore;
        this.iMDbRating = iMDbRating;
        this.iMDbVotes = iMDbVotes;
        this.iMDbID = iMDbID;
        this.type = type;
        this.infoDVD = infoDVD;
        this.boxOfficeResults = boxOfficeResults;
        this.productionCompangne = productionCompangne;
        this.websites = websites;
    }
}
