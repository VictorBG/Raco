package com.victorbg.racofib.view.ui.notes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.view.MainActivity;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.notes.items.NoteItem;
import com.victorbg.racofib.viewmodel.PublicationsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import timber.log.Timber;

public class NotesFragment extends BaseFragment implements Observer<List<Note>>, Injectable {

    @BindView(R.id.recycler_notes)
    RecyclerView recyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.error_state_message)
    TextView errorTextView;

    private ItemAdapter<NoteItem> itemAdapter;
    FastAdapter<NoteItem> fastAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private PublicationsViewModel publicationsViewModel;

    private MainActivity m;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        publicationsViewModel = ViewModelProviders.of(this, viewModelFactory).get(PublicationsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRecycler();
        swipeRefreshLayout.setOnRefreshListener(this::reload);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(this::reload, 100);
    }

    private void setRecycler() {

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        fastAdapter.withEventHook(new ClickEventHook<NoteItem>() {
            @Override
            public void onClick(View v, int position, FastAdapter<NoteItem> fastAdapter, NoteItem item) {
                Intent intent = new Intent(getContext(), NoteDetail.class);
                //TODO: Put KEY in a more visible scope
                intent.putExtra("NoteParam", item.getNote());
                NotesFragment.this.startActivity(intent);
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

//        Drawable addToFav = getContext().getDrawable(R.drawable.ic_favorite_border_black_24dp);
//        Drawable removeFromFav = getContext().getDrawable(R.drawable.ic_remove_fav);
//        int addToFavColor = getContext().getResources().getColor(R.color.md_green_400);
//        int removeFromFavColor = getContext().getResources().getColor(R.color.md_red_400);

//        SwipeCallback simpleSwipeCallback = new SwipeCallback((pos, dir) -> {
//            fastAdapter.notifyItemChanged(pos);
//
//            publicationsViewModel.addToFav(itemAdapter.getAdapterItem(pos).getNote());
//            boolean fav = itemAdapter.getAdapterItem(pos).getNote().favorite;
//            itemAdapter.getAdapterItem(pos).getNote().favorite = !fav;
//
//            if (m != null) {
//                m.showSnackbar(fav ? "Added to favorites" : "Removed from favorites");
//            } else {
//                Toast.makeText(getContext(), fav ? "Added to favorites" : "Removed from favorites", Toast.LENGTH_SHORT).show();
//            }
//        }, new SwipeCallback.ItemSwipeDrawableCallback() {
//            @Override
//            public Drawable getDrawable(int position) {
//                return itemAdapter.getAdapterItem(position).getNote().favorite ? removeFromFav : addToFav;
//            }
//
//            @Override
//            public int getColor(int position) {
//                return itemAdapter.getAdapterItem(position).getNote().favorite ? removeFromFavColor : addToFavColor;
//            }
//        });
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleSwipeCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastAdapter);

    }

    private void reload() {
        publicationsViewModel.getPublications().removeObservers(this);
        publicationsViewModel.reload();
        swipeRefreshLayout.setRefreshing(true);
        publicationsViewModel.getPublications().observe(this, listResource -> {

            Timber.d("Data observed with status %s and time %d", listResource.status.toString(), System.currentTimeMillis());
            onChangedState(listResource.status, listResource.message);
            onChanged(listResource.data);

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        publicationsViewModel.getPublications().removeObservers(this);
    }

    public void onChanged(List<Note> notes) {
        if (notes == null || notes.isEmpty()) return;
        List<NoteItem> items = new ArrayList<>();
        for (Note note : notes) {
            items.add(new NoteItem().withNote(note));
        }

        //Prevent recreating the whole list when there are identical items (based on title and subject)
        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
        FastAdapterDiffUtil.set(itemAdapter, diffs);

    }

    private void onChangedState(final Status st, String message) {

        int errorTvVis;
        int animVis = errorTvVis = (st == Status.ERROR) ? View.VISIBLE : View.GONE;
        int rvVis = (st == Status.ERROR) ? View.INVISIBLE : View.VISIBLE;

        errorTextView.setVisibility(errorTvVis);
        errorTextView.setText(message);
        animationView.setVisibility(animVis);
        recyclerView.setVisibility(rvVis);
        if (animVis == View.VISIBLE) {
            animationView.playAnimation();
        }
        swipeRefreshLayout.setRefreshing(st == Status.LOADING);
    }

    @Override
    public void onFabSelected() {
        startActivity(new Intent(getContext(), NotesFavoritesActivity.class));
    }
}
