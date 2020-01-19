package com.victorbg.racofib.view.ui.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.domain.subjects.ChangeColorSubjectUseCase;
import com.victorbg.racofib.domain.subjects.LoadSubjectsUseCase;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.ui.settings.items.ColorSettingsItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;


public class ColorSettingsActivity extends BaseActivity implements Injectable, ColorPickerDialogListener {

    @Inject
    LoadSubjectsUseCase loadSubjectsUseCase;
    @Inject
    ChangeColorSubjectUseCase changeColorSubjectUseCase;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ItemAdapter<ColorSettingsItem> itemAdapter;
    private FastAdapter<ColorSettingsItem> fastAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_colors);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.change_colors_subjects_activity_title);

        setRecycler();

        loadSubjectsUseCase.execute().observe(this, this::handleSubjects);

    }

    private void handleSubjects(List<Subject> subjects) {
        List<ColorSettingsItem> items = new ArrayList<>();
        for (Subject s : subjects) {
            items.add(new ColorSettingsItem().withSubject(s));
        }

        itemAdapter.setNewList(items);

//        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
//        FastAdapterDiffUtil.set(itemAdapter, diffs);
    }

    private void setRecycler() {
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));
        fastAdapter.withSelectable(false);
        fastAdapter.withEventHook(new ClickEventHook<ColorSettingsItem>() {
            @Override
            public void onClick(@NotNull View v, int position, @NotNull FastAdapter<ColorSettingsItem> fastAdapter, @NotNull ColorSettingsItem item) {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(position)
                        .setColor(Color.parseColor(item.getSubject().color))
                        .setShowAlphaSlider(false)
                        .show(ColorSettingsActivity.this);
            }

            @javax.annotation.Nullable
            @Override
            public View onBind(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.itemView;
            }

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapter);
    }


    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected int getLightTheme() {
            return R.style.AppTheme_SubjectDetail_Light;
    }

    @Override
    protected int getDarkTheme() {
        return R.style.AppTheme_SubjectDetail_Dark;
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        Subject subject = fastAdapter.getItem(dialogId).getSubject();
        subject.color = String.format("#%06X", 0xFFFFFF & color);
        changeColorSubjectUseCase.execute(subject);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
