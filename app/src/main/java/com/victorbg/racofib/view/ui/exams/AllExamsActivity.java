package com.victorbg.racofib.view.ui.exams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.viewmodel.HomeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import static com.victorbg.racofib.view.ui.exams.ExamDetail.EXAM_PARAM_KEY;

public class AllExamsActivity extends BaseActivity implements Injectable {

    @BindView(R.id.recycler_notes)
    RecyclerView recyclerView;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.error_state_message)
    TextView errorTextView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ItemAdapter<FullExamItem> itemAdapter;
    private FastAdapter<FullExamItem> fastAdapter;

    private HomeViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_notes);

        this.savedInstanceState = savedInstanceState;

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.exams_title);

        setRecycler();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(fastAdapter.saveInstanceState(outState));
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getExams().observe(this, this::handleExams);
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.getExams().removeObservers(this);
    }

    private void setRecycler() {
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        fastAdapter.withEventHook(new ClickEventHook<FullExamItem>() {
            @Override
            public void onClick(@NotNull View v, int position, @NotNull FastAdapter<FullExamItem> fastAdapter, @NotNull FullExamItem item) {

                Intent intent = new Intent(AllExamsActivity.this, ExamDetail.class);
                intent.putExtra(EXAM_PARAM_KEY, item.getExam());
                startActivity(intent);
            }

            @javax.annotation.Nullable
            @Override
            public View onBind(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof FullExamItem.ViewHolder) {
                    return ((FullExamItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapter);
    }

    private void handleExams(Resource<List<Exam>> resource) {
        switch (resource.status) {
            case SUCCESS:
                bindExams(resource.data);
            default:
                break;
        }
    }

    private void bindExams(List<Exam> exams) {
        if (exams == null || exams.size() == 0) {
            showNoContent();
            return;
        }
        List<FullExamItem> items = new ArrayList<>();
        for (Exam exam : exams) {
            items.add(new FullExamItem().withExam(exam));
        }

        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
        FastAdapterDiffUtil.set(itemAdapter, diffs);

        if (savedInstanceState != null) {
            fastAdapter.withSavedInstanceState(savedInstanceState);
        }
    }

    private void showNoContent() {
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        errorTextView.setText("No content");
        errorTextView.setVisibility(View.VISIBLE);
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
        return R.style.AppTheme_NoteDetail_Light;
    }

    @Override
    protected int getDarkTheme() {
        return R.style.AppTheme_NoteDetail_Dark;
    }
}
