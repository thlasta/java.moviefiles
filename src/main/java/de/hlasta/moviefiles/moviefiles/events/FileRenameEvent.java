package de.hlasta.moviefiles.moviefiles.events;

import de.hlasta.moviefiles.moviefiles.persistence.FileRenameEventTypes;
import de.hlasta.moviefiles.moviefiles.publisher.CustomEventPublisher;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class FileRenameEvent<T> extends ApplicationEvent {
    private T what;
    private T whereto;
    private String errorMessage;
    protected FileRenameEventTypes type;

    public FileRenameEvent(CustomEventPublisher customEventPublisher, FileRenameEventTypes type) {
        super(type);
        this.what = (T) customEventPublisher;
        this.whereto = null;
        this.type = type;
    }

    public FileRenameEvent(Object o, FileRenameEventTypes type) {
        super(o);
        this.what = (T) o;
        this.whereto = null;
        this.type = type;
    }

    public FileRenameEvent(Object whereTo, Object what, FileRenameEventTypes type) {
        super(what);
        this.what = (T) what;
        this.whereto = (T) whereTo;
        this.type = type;
    }

    public FileRenameEvent(Object whereTo, Object what, String errorMessage, FileRenameEventTypes type) {
        super(what);
        this.what = (T) what;
        this.whereto = (T) whereTo;
        this.errorMessage = errorMessage;
        this.type = type;
    }

    // ... standard getters
}

