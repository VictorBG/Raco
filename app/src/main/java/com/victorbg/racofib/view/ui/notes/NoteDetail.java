package com.victorbg.racofib.view.ui.notes;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.domain.notes.NotesChangeFavoriteStateUseCase;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.widgets.attachments.AttachmentsGroup;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import butterknife.BindView;

public class NoteDetail extends BaseActivity implements Injectable {

    public static final String NOTE_PARAM = "NoteParam";
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.chipGroup)
    AttachmentsGroup chipGroup;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.content)
    ConstraintLayout content;

    @Inject
    NotesChangeFavoriteStateUseCase changeFavoriteStateUseCase;

    private Note note;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);


        postponeEnterTransition();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().getExtras() != null) {

            note = getIntent().getExtras().getParcelable(NOTE_PARAM);
            if (note == null) finish();

            setTitle(note.subject);

            appBarLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(note.color)));
            title.setText(note.title);
            description.setText(Html.fromHtml(note.text.replaceAll("\n", "<br>")));
            description.setMovementMethod(new LinkMovementMethod());

            if (note.attachments.size() == 0) {
                chipGroup.setVisibility(View.GONE);
            } else {
                chipGroup.setVisibility(View.VISIBLE);
                chipGroup.setAttachments(note.attachments);
            }

            invalidateOptionsMenu();
            startEnterAnimation();
            startPostponedEnterTransition();
        } else {
            finish();
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (note == null) return false;
        menu.clear();
        menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, note.favorite ? getString(R.string.remove_from_favorites_action) : getString(R.string.add_to_favorites_action))
                .setIcon(note.favorite ? R.drawable.ic_favorite_white : R.drawable.ic_favorite_border_white)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == Menu.FIRST) {
            note = changeFavoriteStateUseCase.execute(note);
            invalidateOptionsMenu();
            showSnackbar(note.favorite ? getString(R.string.added_to_favorites) : getString(R.string.removed_from_favorites));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startExitTransition();

        finishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void startEnterAnimation() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(250);
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(bounds);
        transitionSet.addTransition(new Fade(Fade.IN).setDuration(250));
        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        getWindow().setSharedElementEnterTransition(transitionSet);

        content.setAlpha(0);
        appBarLayout.setAlpha(0);

        appBarLayout.animate().setDuration(200).alpha(1.0f).setStartDelay(50).setInterpolator(new AccelerateInterpolator()).start();
        content.animate().setDuration(100).alpha(1.0f).setStartDelay(100).setInterpolator(new AccelerateInterpolator()).start();
    }

    private void startExitTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(250);

//        TransitionSet transitionSet = new TransitionSet();
//        transitionSet.addTransition(bounds);
//        transitionSet.addTransition(new Fade(Fade.OUT).setDuration(100));
//        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
//        getWindow().setSharedElementReturnTransition(transitionSet);
        appBarLayout.animate().setDuration(200).alpha(.0f).setInterpolator(new AccelerateInterpolator()).start();
        content.animate().setDuration(100).alpha(.0f).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    protected int getLightTheme() {
        return R.style.AppTheme_NoteDetail_Light;
    }

    @Override
    protected int getDarkTheme() {
        return R.style.AppTheme_NoteDetail_Dark;
    }
}
