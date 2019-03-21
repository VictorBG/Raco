package com.victorbg.racofib.view.ui.notes;

import android.os.Bundle;

import com.mikepenz.fastadapter.commons.utils.DiffCallback;
import com.victorbg.racofib.view.ui.notes.items.NoteItem;

import androidx.annotation.Nullable;

public class NotesDiffCallback implements DiffCallback<NoteItem> {

    @Override
    public boolean areItemsTheSame(NoteItem oldItem, NoteItem newItem) {
        return oldItem.getIdentifier() == newItem.getIdentifier();
    }

    @Override
    public boolean areContentsTheSame(NoteItem oldItem, NoteItem newItem) {
        return oldItem.getNote().equals(newItem.getNote());
    }

    @Nullable
    @Override
    public Object getChangePayload(NoteItem oldItem, int oldItemPosition, NoteItem newItem, int newItemPosition) {
        Bundle result = new Bundle();

        if (!newItem.getNote().color.equalsIgnoreCase(oldItem.getNote().color)) {
            result.putString("color", newItem.getNote().color);
        }

//        if (newItem.getNote().favorite != oldItem.getNote().favorite) {
//            result.putBoolean("favorite", !oldItem.getNote().favorite);
//        }

        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
}
