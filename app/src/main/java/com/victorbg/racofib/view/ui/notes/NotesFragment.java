package com.victorbg.racofib.view.ui.notes;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.databinding.FragmentNotesBinding;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.utils.ConsumableBoolean;
import com.victorbg.racofib.utils.DisplayUtils;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.transitions.SlideExplode;
import com.victorbg.racofib.view.ui.notes.items.NoteItem;
import com.victorbg.racofib.viewmodel.PublicationsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.saket.inboxrecyclerview.InboxRecyclerView;
import me.saket.inboxrecyclerview.page.ExpandablePageLayout;
import me.saket.inboxrecyclerview.page.PageStateChangeCallbacks;
import timber.log.Timber;

public class NotesFragment extends BaseFragment implements Observer<List<Note>>, Injectable {

    @BindView(R.id.recycler_notes)
    InboxRecyclerView recyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.error_state_message)
    TextView errorTextView;
    @BindView(R.id.notePageLayout)
    ExpandablePageLayout notePageLayout;


    private ItemAdapter<NoteItem> itemAdapter;
    FastAdapter<NoteItem> fastAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private PublicationsViewModel publicationsViewModel;

    private final ConsumableBoolean scheduledScrollToTop = new ConsumableBoolean(true);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentNotesBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false);
        ButterKnife.bind(this, binding.getRoot());
        binding.setLifecycleOwner(this);
        publicationsViewModel = ViewModelProviders.of(this, viewModelFactory).get(PublicationsViewModel.class);
        binding.setNotesViewModel(publicationsViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(() -> reload(true));
        publicationsViewModel.orderAscending.observe(this, data -> scheduledScrollToTop.setValue(true));
        setRecycler();
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
            public void onClick(@NotNull View v, int position, @NotNull FastAdapter<NoteItem> fastAdapter, @NotNull NoteItem item) {
//                Intent intent = new Intent(getContext(), DialogNoteDetail.class);
//                intent.putExtra(NoteDetail.NOTE_PARAM, item.getNote());
//                ActivityOptions activityOptions = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
//                NotesFragment.this.startActivity(intent, activityOptions.toBundle());
                publicationsViewModel.selectedNote.setValue(item.getNote());
                recyclerView.expandItem(item.getIdentifier());
                getMainActivity().hideToolbar();
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

        itemAdapter.getItemFilter().withFilterPredicate((item, constraint) -> item.getNote().title.toLowerCase().contains(constraint.toString().toLowerCase()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastAdapter);
        recyclerView.setExpandablePage(notePageLayout);
        notePageLayout.setPullToCollapseThresholdDistance((int) DisplayUtils.convertDpToPixel(56f));
        notePageLayout.addStateChangeCallbacks(new PageStateChangeCallbacks() {
            @Override
            public void onPageAboutToExpand(long l) {

            }

            @Override
            public void onPageExpanded() {
                swipeRefreshLayout.setEnabled(false);
            }

            @Override
            public void onPageAboutToCollapse(long l) {

            }

            @Override
            public void onPageCollapsed() {
                swipeRefreshLayout.setEnabled(true);
                getMainActivity().showToolbar();
            }
        });

    }

    private void reload() {
        reload(false);
    }

    private void reload(boolean force) {
        publicationsViewModel.getPublications().removeObservers(this);
        publicationsViewModel.reload(force);
        publicationsViewModel.getPublications().observe(this, listResource -> {
            Timber.d("Data observed with status %s and time %d", listResource.status.toString(), System.currentTimeMillis());
            onChangedState(listResource.status);
            onChanged(listResource.data);
        });
    }

    public void onChanged(List<Note> notes) {
        if (notes == null || notes.isEmpty()) return;
        List<NoteItem> items = new ArrayList<>();
        for (Note note : notes) {
            items.add(new NoteItem().withNote(note));
        }

        int oldListSize = itemAdapter.getAdapterItemCount();
        //Prevent recreating the whole list when there are identical items (based on title and subject)
        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
        FastAdapterDiffUtil.set(itemAdapter, diffs);

        if (oldListSize != notes.size() || scheduledScrollToTop.getValue()) {
            recyclerView.scrollToPosition(0);
        }

    }

    private void onChangedState(final Status st) {
        boolean refresh = st == Status.LOADING;
        if (swipeRefreshLayout.isRefreshing() == refresh) return;
        swipeRefreshLayout.setRefreshing(refresh);
    }

    @OnClick(R.id.closeItem)
    public void closeItem(View v) {
        if (notePageLayout.isExpanded()) {
            recyclerView.collapse();
        }
    }

    @Override
    public void onFabSelected() {
        startActivity(new Intent(getContext(), NotesFavoritesActivity.class));
    }

    @Override
    public void onQuery(String query) {
        itemAdapter.filter(query);
    }

    @Override
    public void onFilterSelected() {
        publicationsViewModel.onFilterClick();
    }
}

