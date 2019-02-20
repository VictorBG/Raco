package com.victorbg.racofib.view.ui.notes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.ui.notes.items.NoteItem;
import com.victorbg.racofib.view.widgets.SwipeCallback;
import com.victorbg.racofib.viewmodel.PublicationsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import timber.log.Timber;

public class NotesFavoritesActivity extends BaseActivity implements Injectable {

    @BindView(R.id.recycler_notes)
    RecyclerView recyclerView;

    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.error_state_message)
    TextView errorTextView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ItemAdapter<NoteItem> itemAdapter;
    FastAdapter<NoteItem> fastAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private PublicationsViewModel publicationsViewModel;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_notes);

        publicationsViewModel = ViewModelProviders.of(this, viewModelFactory).get(PublicationsViewModel.class);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Favorites");

        setRecycler();
    }

    @Override
    public void onStart() {
        super.onStart();
        reload();
    }

    @Override
    public void onStop() {
        super.onStop();
        publicationsViewModel.getPublications().removeObservers(this);
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

    private void reload() {
        publicationsViewModel.getPublications().removeObservers(this);
        publicationsViewModel.reload();
        publicationsViewModel.getSavedPublications().observe(this, listResource ->
                new Handler().postDelayed(() -> onChanged(listResource), 200));
    }

    private void setRecycler() {

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        fastAdapter.withEventHook(new ClickEventHook<NoteItem>() {
            @Override
            public void onClick(View v, int position, FastAdapter<NoteItem> fastAdapter, NoteItem item) {
                Intent intent = new Intent(NotesFavoritesActivity.this, NoteDetail.class);
                //TODO: Put KEY in a more visible scope
                intent.putExtra("NoteParam", item.getNote());
                NotesFavoritesActivity.this.startActivity(intent);
            }

            @javax.annotation.Nullable
            @Override
            public View onBind(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof NoteItem.ViewHolder) {
                    return ((NoteItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

        });

        fastAdapter.withEventHook(new ClickEventHook<NoteItem>() {
            @Override
            public void onClick(View v, int position, FastAdapter<NoteItem> fastAdapter, NoteItem item) {
                publicationsViewModel.addToFav(item.getNote());
                itemAdapter.remove(position);
                fastAdapter.notifyAdapterItemRemoved(position);
                showSnackbar("Removed from favorites");
            }

            @javax.annotation.Nullable
            @Override
            public View onBind(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof NoteItem.ViewHolder) {
                    return ((NoteItem.ViewHolder) viewHolder).saved;
                }
                return null;
            }

        });

        Drawable removeFromFav = getDrawable(R.drawable.ic_remove_fav);
        int removeFromFavColor = getResources().getColor(R.color.md_red_400);

        SwipeCallback simpleSwipeCallback = new SwipeCallback((pos, dir) -> {
            fastAdapter.notifyItemChanged(pos);
            publicationsViewModel.addToFav(itemAdapter.getAdapterItem(pos).getNote());
            itemAdapter.remove(pos);
            fastAdapter.notifyAdapterItemRemoved(pos);
            showSnackbar("Removed from favorites");


        }, new SwipeCallback.ItemSwipeDrawableCallback() {
            @Override
            public Drawable getDrawable(int position) {
                return removeFromFav;
            }

            @Override
            public int getColor(int position) {
                return removeFromFavColor;
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapter);

    }

    public void onChanged(List<Note> notes) {
        if (notes == null || notes.size() == 0) {
            onChangedState(Status.ERROR, "No content");
            return;
        }
        onChangedState(Status.SUCCESS, null);
        List<NoteItem> items = new ArrayList<>();
        for (Note note : notes) {
            items.add(new NoteItem().withNote(note));
        }

        //Prevent recreating the whole list when there are identical items (based on title and subject)

        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
        FastAdapterDiffUtil.set(itemAdapter, diffs);

//        recyclerView.scrollToPosition(0);
    }

    private void onChangedState(final Status st, String message) {

        int errorTvVis;
        int animVis = errorTvVis = (st == Status.ERROR) ? View.VISIBLE : View.INVISIBLE;
        int rvVis = (st == Status.ERROR) ? View.INVISIBLE : View.VISIBLE;

        errorTextView.setVisibility(errorTvVis);
        errorTextView.setText(message);
        animationView.setVisibility(animVis);
        recyclerView.setVisibility(rvVis);
        if (animVis == View.VISIBLE) {
            animationView.playAnimation();
        }
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
