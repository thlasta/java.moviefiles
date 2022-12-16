package de.hlasta.moviefiles.moviefiles.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Person {
    UUID id;
    String vorname;
    String nachname;
}
