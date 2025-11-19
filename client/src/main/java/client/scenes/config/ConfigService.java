package client.scenes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.NoteCollection;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Service for managing application configuration.
 * Handles reading and writing the configuration file.
 */
@Singleton
public class ConfigService {
    private static final String CONFIG_FILE = "config.json";
    private final ObjectMapper mapper;
    private Config currentConfig;

    @Inject
    public ConfigService() {
        this.mapper = new ObjectMapper();
        loadConfig();
    }

    /**
     * Updates the configuration with a new last selected collection.
     *
     * @param collection the newly selected collection
     * @throws IOException if the configuration cannot be saved
     */
    public void updateLastSelectedCollection(NoteCollection collection) throws IOException {
        if (collection != null) {
            Config newConfig = new Config(
                    currentConfig.defaultCollection(),
                    currentConfig.preferredLanguage(),
                    collection
            );
            updateConfig(newConfig);
        }
    }

    /**
     * Loads the configuration from file.
     * Creates a default configuration if the file doesn't exist or is invalid.
     */
    private void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        try {
            if (configFile.exists()) {
                String json = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
                currentConfig = mapper.readValue(json, Config.class);
            } else {
                currentConfig = Config.createDefault();
                saveConfig();
            }
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
            currentConfig = Config.createDefault();
        }
    }

    /**
     * Saves the current configuration to file.
     *
     * @throws IOException if the file cannot be written
     */
    public void saveConfig() throws IOException {
        String json = mapper.writeValueAsString(currentConfig);
        FileUtils.writeStringToFile(new File(CONFIG_FILE), json, StandardCharsets.UTF_8);
    }

    /**
     * Gets the current configuration.
     *
     * @return the current configuration, never null
     */
    public Config getConfig() {
        return currentConfig;
    }

    /**
     * Updates the configuration and saves it to file.
     *
     * @param config the new configuration
     * @throws IOException if the configuration cannot be saved
     */
    public void updateConfig(Config config) throws IOException {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        this.currentConfig = config;
        saveConfig();
    }
}