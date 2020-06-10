package com.zds.zw.view.com;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.controlsfx.control.Rating;


public class ModelLisibilty {
    private final SimpleStringProperty text;
    private final SimpleObjectProperty rating;

    public ModelLisibilty(String text, Rating rating) {
        this.text = new SimpleStringProperty(text);
        this.rating = new SimpleObjectProperty(rating);
    }

    public String getText() {
        return text.get();
    }

    public Object getRating() {
        return rating.get();
    }
}
