package de.hlasta.moviefiles.moviefiles.events;

import de.hlasta.moviefiles.moviefiles.persistence.FileRenameEventTypes;
import de.hlasta.moviefiles.moviefiles.persistence.FileSearchEventTypes;
import de.hlasta.moviefiles.moviefiles.publisher.CustomEventPublisher;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class FileSearchEvent<T> extends ApplicationEvent {
    private T what;
    private T whereto;
    protected FileSearchEventTypes type;

    public FileSearchEvent(CustomEventPublisher customEventPublisher, FileSearchEventTypes type) {
        super(type);
        this.what = (T) customEventPublisher;
        this.whereto = null;
        this.type = type;
    }

    public FileSearchEvent(Object o, FileSearchEventTypes type) {
        super(o);
        this.what = (T) o;
        this.whereto = null;
        this.type = type;
    }

    public FileSearchEvent(Object whereTo, Object what, FileSearchEventTypes type) {
        super(what);
        this.what = (T) what;
        this.whereto = (T) whereTo;
        this.type = type;
    }

    // ... standard getters
}

