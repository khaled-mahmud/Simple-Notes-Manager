package com.notes.iit.simplenotesmanager;

import java.util.Date;

public class Note {
    public String description;
    public String date;
    public byte[] image;

    public Note(String description, String date, byte[] image) {
        this.description = description;
        this.date = date;
        this.image = image;
    }
}
