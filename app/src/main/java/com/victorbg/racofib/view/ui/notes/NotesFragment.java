package com.victorbg.racofib.view.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.EventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.notes.items.NoteItem;
import com.victorbg.racofib.view.widgets.DividerItemDecoration;
import com.victorbg.racofib.viewmodel.NotesViewModel;

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
    private FastAdapter<NoteItem> fastAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private NotesViewModel notesViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notesViewModel = ViewModelProviders.of(this, viewModelFactory).get(NotesViewModel.class);

        setRecycler();
        swipeRefreshLayout.setOnRefreshListener(this::reload);

        notesViewModel.notesState.observe(this, this::onChangedState);
        notesViewModel.getLiveData().observe(this, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        notesViewModel.reload(false);
    }

    private void setRecycler() {

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        fastAdapter.withEventHook(new ClickEventHook<NoteItem>() {
            @Override
            public void onClick(View v, int position, FastAdapter<NoteItem> fastAdapter, NoteItem item) {
                Intent intent = new Intent(getContext(), NoteDetail.class);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastAdapter);

    }

    private void reload() {
        swipeRefreshLayout.setRefreshing(true);
        notesViewModel.reload(true);
    }

    @Override
    public void onChanged(List<Note> notes) {
        List<NoteItem> items = new ArrayList<>();
        for (Note note : notes) {
            items.add(new NoteItem().withNote(note).withContext(getContext()));
        }

        //Prevent recreating the whole list when there are identical items (based on title and subject)
        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
        FastAdapterDiffUtil.set(itemAdapter, diffs);
        recyclerView.scrollToPosition(0);
    }

    private void onChangedState(NotesViewModel.NotesState notesState) {

        int errorTvVis = View.GONE;
        int animVis = View.GONE;
        int rvVis = View.VISIBLE;
        String errorMessage = "";
        switch (notesState) {
            case EMPTY:
                errorTvVis = animVis = View.VISIBLE;
                rvVis = View.GONE;
                errorMessage = "No content";
                break;
            case ERROR:
                errorTvVis = animVis = View.VISIBLE;
                rvVis = View.GONE;
                errorMessage = "An error has occurred";
                break;
            case LOADING:
                errorTvVis = animVis = View.GONE;
                rvVis = View.GONE;
                break;
            case LOADED:
                errorTvVis = animVis = View.GONE;
                rvVis = View.VISIBLE;
            default:
                break;

        }

        errorTextView.setVisibility(errorTvVis);
        errorTextView.setText(errorMessage);
        recyclerView.setVisibility(rvVis);
        animationView.setVisibility(animVis);
        if (animVis == View.VISIBLE) {
            animationView.playAnimation();
        }
        swipeRefreshLayout.setRefreshing(notesState == NotesViewModel.NotesState.LOADING);
    }
}
