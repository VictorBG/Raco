package com.victorbg.racofib.view.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.notes.items.NoteItem;
import com.victorbg.racofib.view.widgets.DividerItemDecoration;
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

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private PublicationsViewModel publicationsViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        publicationsViewModel = ViewModelProviders.of(this, viewModelFactory).get(PublicationsViewModel.class);
        publicationsViewModel.getPublications().observe(this, listResource -> {
            onChangedState(listResource.status, listResource.message);
            onChanged(listResource.data);
        });

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

    private void setRecycler() {

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        itemAdapter = new ItemAdapter<>();
        FastAdapter<NoteItem> fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastAdapter);
        recyclerView.setNestedScrollingEnabled(false);

    }

    private void reload() {
        //TODO: Should I remove the old observer here? Anyway, the old livedata will not exists anymore
        publicationsViewModel.reload();
        publicationsViewModel.getPublications().observe(this, listResource -> {
            onChangedState(listResource.status, listResource.message);
            onChanged(listResource.data);
        });
    }

    public void onChanged(List<Note> notes) {
        if (notes == null) return;
        List<NoteItem> items = new ArrayList<>();
        for (Note note : notes) {
            items.add(new NoteItem().withNote(note).withContext(getContext()));
        }

        //Prevent recreating the whole list when there are identical items (based on title and subject)
        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
        FastAdapterDiffUtil.set(itemAdapter, diffs);
        recyclerView.scrollToPosition(0);
    }

    private void onChangedState(final Status st, String message) {

        int errorTvVis;
        int animVis = errorTvVis = (st == Status.ERROR) ? View.VISIBLE : View.GONE;

        errorTextView.setVisibility(errorTvVis);
        errorTextView.setText(message);
        animationView.setVisibility(animVis);
        if (animVis == View.VISIBLE) {
            animationView.playAnimation();
        }
        swipeRefreshLayout.setRefreshing(st == Status.LOADING);
    }
}
