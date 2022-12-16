package de.hlasta.moviefiles.moviefiles.publisher;

import de.hlasta.moviefiles.moviefiles.events.TmdbSearchEvent;
import de.hlasta.moviefiles.moviefiles.persistence.TmdbSearchEventTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CustomEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher = new ApplicationEventPublisher() {
        @Override
        public void publishEvent(Object event) {
            System.out.println("ApplicationEventPublisher.publishEvent : Publishing custom event: " + event.toString());
        }
    };

    public void publishCustomEvent(final TmdbSearchEventTypes type) {
        System.out.println("Publishing custom event. ");
        TmdbSearchEvent customSpringEvent = new TmdbSearchEvent(this, type);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}