package com.victorbg.racofib.data.notification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import androidx.work.ListenableWorker;

import com.application.isradeleon.notify.Notify;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.utils.Utils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class NotificationCenter {

    public static void showNotesNotification(Context context, List<Note> notes) {
        int size = notes.size();

        if (size == 1) {
            showSingleNote(context, notes.get(0));
        } else if (size > 1 && size < 4) {
            showMultipleNotes(context, notes);
        } else if (size >= 4) {
            showGeneralNotes(context, notes);
        }
    }

    private static void showGeneralNotes(Context context, List<Note> notes) {
        Notify.create(context)
                .setTitle(String.format(Locale.getDefault(), "There are %d new publications", notes.size()))
                .setContent(String.format(Locale.getDefault(), "%d new publications from %s",
                        notes.size(),
                        Utils.getSubjectNames(notes)))
                .setImportance(Notify.NotificationImportance.HIGH)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(R.drawable.ic_notification)
                .show();
    }

    private static void showMultipleNotes(Context context, List<Note> notes) {
        String messageBody = notes
                .stream()
                .map(n -> n.title.substring(0, 30) + "...")
                .collect(Collectors.joining("\n"));
        Notify.create(context)
                .setTitle(String.format(Locale.getDefault(), "There are %d new publications", notes.size()))
                .setContent(messageBody)
                .setImportance(Notify.NotificationImportance.HIGH)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(R.drawable.ic_notification)
                .show();
    }

    @SuppressLint("ResourceType")
    private static void showSingleNote(Context context, Note note) {
        Notify.create(context)
                .setTitle(String.format(Locale.getDefault(), "There is a new publication from %s", note.subject))
                .setContent(note.title.substring(0, 80) + "...")
                .setColor(Color.parseColor(note.color))
                .setImportance(Notify.NotificationImportance.HIGH)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(R.drawable.ic_notification)
                .show();
    }
}
