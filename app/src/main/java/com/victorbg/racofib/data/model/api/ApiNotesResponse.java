package com.victorbg.racofib.data.model.api;

import com.google.gson.annotations.SerializedName;
import com.victorbg.racofib.data.model.notes.Note;

import java.util.List;


public class ApiNotesResponse {
    @SerializedName("results")
    private List<Note> items;

    public List<Note> getItems() {
        return items;
    }

    public void setItems(List<Note> items) {
        this.items = items;
    }
}
