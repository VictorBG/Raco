package com.victorbg.racofib.view.ui.notes;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import butterknife.BindDimen;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.AppBarLayout;
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
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.notes.items.NoteItem;
import com.victorbg.racofib.viewmodel.PublicationsViewModel;

import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
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
import me.saket.inboxrecyclerview.page.InterceptResult;
import me.saket.inboxrecyclerview.page.PageStateChangeCallbacks;

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

  @BindView(R.id.nested_scroll_note)
  NestedScrollView scrollView;

  @BindView(R.id.appBarLayout)
  AppBarLayout appBarLayout;

  @BindView(R.id.searchEditText)
  AppCompatEditText searchEditText;

  @BindView(R.id.closeSearch)
  ImageView closeSearch;

  @BindDimen(R.dimen.refresher_offset)
  int refreshOffset;

  private ItemAdapter<NoteItem> itemAdapter;
  FastAdapter<NoteItem> fastAdapter;

  @Inject ViewModelProvider.Factory viewModelFactory;

  private PublicationsViewModel publicationsViewModel;
  private final ConsumableBoolean scheduledScrollToTop = new ConsumableBoolean(true);
  private List<Note> scheduledUpdate = null;

  private OnBackPressedCallback onBackPressedCallback =
      new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
          if (notePageLayout.isExpanded()) {
            recyclerView.collapse();
          }
        }
      };

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    FragmentNotesBinding binding =
        DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false);
    ButterKnife.bind(this, binding.getRoot());
    binding.setLifecycleOwner(this);
    publicationsViewModel =
        ViewModelProviders.of(this, viewModelFactory).get(PublicationsViewModel.class);
    binding.setNotesViewModel(publicationsViewModel);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    swipeRefreshLayout.setProgressViewOffset(
        false, refreshOffset, refreshOffset + (swipeRefreshLayout.getProgressViewEndOffset() / 2));
    swipeRefreshLayout.setOnRefreshListener(() -> reload(true));
    publicationsViewModel.orderAscending.observe(this, data -> scheduledScrollToTop.setValue(true));

    setRecycler();

    swipeRefreshLayout.setColorSchemeColors(
        ContextCompat.getColor(getContext(), R.color.accent),
        ContextCompat.getColor(getContext(), R.color.primary));

    publicationsViewModel
        .getPublications()
        .observe(
            this,
            listResource -> {
              onChangedState(listResource.status);
              onChanged(listResource.data);
            });

    searchEditText.setOnFocusChangeListener(
        (v, hasFocus) -> closeSearch.setVisibility(hasFocus ? View.VISIBLE : View.GONE));
    searchEditText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            itemAdapter.filter(s);
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });
  }

  @Override
  public void onResume() {
    super.onResume();
    new Handler().postDelayed(this::reload, 100);
  }

  private void setRecycler() {

    itemAdapter = new ItemAdapter<>();
    fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

    fastAdapter.withEventHook(
        new ClickEventHook<NoteItem>() {
          @Override
          public void onClick(
              @NotNull View v,
              int position,
              @NotNull FastAdapter<NoteItem> fastAdapter,
              @NotNull NoteItem item) {

            publicationsViewModel.selectedNote.setValue(item.getNote());
            recyclerView.expandItem(item.getIdentifier());
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

    itemAdapter
        .getItemFilter()
        .withFilterPredicate(
            (item, constraint) ->
                item.getNote().title.toLowerCase().contains(constraint.toString().toLowerCase()));
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(fastAdapter);
    recyclerView.setExpandablePage(notePageLayout);
    notePageLayout.pushParentToolbarOnExpand(appBarLayout);
    notePageLayout.setPullToCollapseInterceptor(
        (downX, downY, upwardPull) ->
            scrollView.canScrollVertically(upwardPull ? 1 : -1)
                ? InterceptResult.INTERCEPTED
                : InterceptResult.IGNORED);
    notePageLayout.addStateChangeCallbacks(
        new PageStateChangeCallbacks() {
          @Override
          public void onPageAboutToExpand(long l) {
            appBarLayout.setExpanded(true, false);
            swipeRefreshLayout.setEnabled(false);
            requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(NotesFragment.this, onBackPressedCallback);
          }

          @Override
          public void onPageExpanded() {}

          @Override
          public void onPageAboutToCollapse(long l) {
            swipeRefreshLayout.setEnabled(true);
          }

          @Override
          public void onPageCollapsed() {
            onBackPressedCallback.remove();
            scrollView.scrollTo(0, 0);
            if (scheduledUpdate != null) {
              onChanged(scheduledUpdate);
              scheduledUpdate = null;
            }
          }
        });
  }

  private void reload() {
    reload(false);
  }

  private void reload(boolean force) {
    publicationsViewModel.reload(force);
  }

  public void onChanged(List<Note> notes) {
    if (notes == null || notes.isEmpty()) {
      return;
    }

    if (notePageLayout.isExpanded()) {
      scheduledUpdate = new ArrayList<>(notes);
      return;
    }

    List<NoteItem> items =
        notes.stream()
            .filter((note) -> !note.subject.contains("#"))
            .map((note) -> new NoteItem().withNote(note))
            .collect(Collectors.toList());

    int oldListSize = itemAdapter.getAdapterItemCount();
    // Prevent recreating the whole list when there are identical items
    DiffUtil.DiffResult diffs =
        FastAdapterDiffUtil.calculateDiff(itemAdapter, items, new NotesDiffCallback());
    FastAdapterDiffUtil.set(itemAdapter, diffs);

    if (oldListSize != notes.size() || scheduledScrollToTop.getValue()) {
      recyclerView.scrollToPosition(0);
    }
  }

  private void onChangedState(final Status st) {
    boolean refresh = st == Status.LOADING;
    if (swipeRefreshLayout.isRefreshing() != refresh) {
      swipeRefreshLayout.setRefreshing(refresh);
    }
  }

  @OnClick(R.id.closeItem)
  public void closeItem(View v) {
    if (notePageLayout.isExpanded()) {
      recyclerView.collapse();
    }
  }

  @OnClick(R.id.closeSearch)
  public void closeSearch(View v) {
    searchEditText.setText(null);
    hideKeyboard(searchEditText);
    searchEditText.clearFocus();
  }

  @Override
  public boolean onItemClick(int id) {
    if (id == R.id.filter_menu) {
      publicationsViewModel.onFilterClick();
    }
    return super.onItemClick(id);
  }
}
