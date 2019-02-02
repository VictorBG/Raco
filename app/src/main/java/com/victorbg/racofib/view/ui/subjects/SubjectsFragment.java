package com.victorbg.racofib.view.ui.subjects;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.Subject;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.notes.NoteDetail;
import com.victorbg.racofib.view.ui.notes.NotesFragment;
import com.victorbg.racofib.view.ui.notes.items.NoteItem;
import com.victorbg.racofib.view.ui.subjects.items.SubjectItem;
import com.victorbg.racofib.view.widgets.DividerItemDecoration;
import com.victorbg.racofib.viewmodel.PublicationsViewModel;
import com.victorbg.racofib.viewmodel.SubjectsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

public class SubjectsFragment extends BaseFragment implements Injectable {

    @BindView(R.id.recycler_notes)
    RecyclerView recyclerView;

    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.error_state_message)
    TextView errorTextView;

    private SubjectsViewModel subjectsViewModel;
    private ItemAdapter<SubjectItem> itemAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subjectsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SubjectsViewModel.class);
        subjectsViewModel.getSubjects().observe(this, this::onChanged);
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
    }

    private void setRecycler() {

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        itemAdapter = new ItemAdapter<>();
        FastAdapter<SubjectItem> fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        fastAdapter.withEventHook(new ClickEventHook<SubjectItem>() {
            @Override
            public void onClick(View v, int position, FastAdapter<SubjectItem> fastAdapter, SubjectItem item) {
                Toast.makeText(getContext(), item.getSubject().name, Toast.LENGTH_SHORT).show();
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

    public void onChanged(List<Subject> list) {
        if (list == null || list.isEmpty()) {
            setLayout(true);
            return;
        }
        setLayout(false);
        List<SubjectItem> items = new ArrayList<>();
        for (Subject subject : list) {
            items.add(new SubjectItem().withSubject(subject));
        }

        //Prevent recreating the whole list when there are identical items
        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
        FastAdapterDiffUtil.set(itemAdapter, diffs);
        recyclerView.scrollToPosition(0);
    }

    private void setLayout(boolean empty) {

        int errorTvVis;
        int animVis = errorTvVis = (empty) ? View.VISIBLE : View.GONE;
        errorTextView.setVisibility(errorTvVis);
        errorTextView.setText("No content");
        animationView.setVisibility(animVis);
        if (empty) {
            animationView.playAnimation();
        }

    }
}
