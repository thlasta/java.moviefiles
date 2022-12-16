package de.hlasta.moviefiles.moviefiles.events;

import de.hlasta.moviefiles.moviefiles.persistence.TmdbSearchEventTypes;
import de.hlasta.moviefiles.moviefiles.publisher.CustomEventPublisher;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class TmdbSearchEvent<T> extends ApplicationEvent {
    private T what;
    private T whereto;
    protected TmdbSearchEventTypes type;

    public TmdbSearchEvent(CustomEventPublisher customEventPublisher, TmdbSearchEventTypes type) {
        super(type);
        this.what = (T) customEventPublisher;
        this.whereto = null;
        this.type = type;
    }

    public TmdbSearchEvent(Object o, TmdbSearchEventTypes type) {
        super(o);
        this.what = (T) o;
        this.whereto = null;
        this.type = type;
    }

    public TmdbSearchEvent(Object whereTo, Object what, TmdbSearchEventTypes type) {
        super(what);
        this.what = (T) what;
        this.whereto = (T) whereTo;
        this.type = type;
    }

    // ... standard getters
}

