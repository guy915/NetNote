package client;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Flag {
    private final String langCode;
    private final ImageView image;

    /**
     * Constructor for the flag class
     *
     * @param langCode language of the flag
     */
    public Flag(String langCode) {
        this.langCode = langCode;
        this.image = new ImageView(new Image("client/i18n/" + langCode + ".png"));
        this.image.setFitWidth(50);
        this.image.setFitHeight(30);
        this.image.setPreserveRatio(true);
    }

    /**
     * Getter for the language code
     *
     * @return langCode
     */
    public String getLangCode() {
        return langCode;
    }

    /**
     * Getter for the image
     *
     * @return image
     */
    public ImageView getImage() {
        return image;
    }

    /**
     * Equals method for the flag class
     *
     * @param o object to compare to
     * @return true or false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flag flag = (Flag) o;
        return Objects.equals(langCode, flag.langCode) && Objects.equals(image, flag.image);
    }

    /**
     * Hashcode of the flag class
     *
     * @return integer number hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(langCode, image);
    }
}
