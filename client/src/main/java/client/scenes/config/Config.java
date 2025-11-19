package client.scenes.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import commons.NoteCollection;

/**
 * Configuration class that stores user preferences.
 * This class is used to persist settings across application restarts.
 *
 * @param defaultCollection      the default note collection
 * @param preferredLanguage      the preferred language code (e.g., "en", "nl", "de")
 * @param lastSelectedCollection the last selected collection by the user
 */
public record Config(NoteCollection defaultCollection, String preferredLanguage,
                     NoteCollection lastSelectedCollection) {
    /**
     * Creates a new Config instance.
     *
     * @param defaultCollection the default note collection
     * @param preferredLanguage the preferred language code (e.g., "en", "nl", "de")
     */
    @JsonCreator
    public Config(
            @JsonProperty("defaultCollection") NoteCollection defaultCollection,
            @JsonProperty("preferredLanguage") String preferredLanguage,
            @JsonProperty("lastSelectedCollection") NoteCollection lastSelectedCollection) {
        this.defaultCollection = defaultCollection;
        this.preferredLanguage = preferredLanguage != null ? preferredLanguage : "en";
        this.lastSelectedCollection = lastSelectedCollection;
    }

    /**
     * Creates a default configuration.
     *
     * @return a new Config instance with default values
     */
    public static Config createDefault() {
        return new Config(null, "en", null);
    }

    @Override
    public NoteCollection lastSelectedCollection() {
        return lastSelectedCollection;
    }

    /**
     * Gets the default note collection.
     *
     * @return the default collection, or null if none is set
     */
    @Override
    public NoteCollection defaultCollection() {
        return defaultCollection;
    }

    /**
     * Gets the preferred language code.
     *
     * @return the language code, never null
     */
    @Override
    public String preferredLanguage() {
        return preferredLanguage;
    }

}