package de.hlasta.moviefiles.moviefiles.persistence;

public class Tuple<T1, T2> {
    public final T1 ratio;
    public final T2 movieDb;

    public Tuple(T1 ratio, T2 movieDb) {
        this.ratio = ratio;
        this.movieDb = movieDb;
    }

    public static <T1, T2> Tuple<T1, T2> of(T1 _1, T2 _2) {
        return new Tuple<>(_1, _2);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", ratio, movieDb);
    }
}
