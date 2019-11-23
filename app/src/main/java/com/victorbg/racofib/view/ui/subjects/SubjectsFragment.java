package com.victorbg.racofib.view.ui.subjects;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.glide.GlideRequests;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.login.LoginActivity;
import com.victorbg.racofib.view.ui.settings.SettingsActivity;
import com.victorbg.racofib.view.ui.subjects.SubjectsFragmentDirections.ActionSubjectsFragmentToSubjectDetailFragment2;
import com.victorbg.racofib.view.ui.subjects.items.SubjectItem;
import com.victorbg.racofib.viewmodel.SubjectsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.OnClick;

import org.jetbrains.annotations.NotNull;

//TODO: refactor this into ProfileFragment
public class SubjectsFragment extends BaseFragment implements Injectable {

    @BindView(R.id.recycler_notes)
    RecyclerView recyclerView;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.error_state_message)
    TextView errorTextView;
    @BindView(R.id.profilePicture)
    ImageView profilePicture;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.mail)
    TextView mail;

    private ItemAdapter<SubjectItem> itemAdapter;
    private SubjectsViewModel subjectsViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    GlideRequests glideRequests;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subjectsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SubjectsViewModel.class);
        subjectsViewModel.getSubjects().observe(this, this::onChanged);
        subjectsViewModel.getUser().observe(this, (user) ->
                Optional.ofNullable(user).ifPresent(u -> {
                    name.setText(u.getFullname());
                    mail.setText(u.mail);
                    glideRequests.loadImage(profilePicture, u.photoUrl);
                }));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRecycler();
    }

    private void setRecycler() {
        itemAdapter = new ItemAdapter<>();
        FastAdapter<SubjectItem> fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        fastAdapter.withEventHook(new ClickEventHook<SubjectItem>() {
            @Override
            public void onClick(@NotNull View v, int position, @NotNull FastAdapter<SubjectItem> fastAdapter,
                                @NotNull SubjectItem item) {
                ActionSubjectsFragmentToSubjectDetailFragment2 action = SubjectsFragmentDirections
                        .actionSubjectsFragmentToSubjectDetailFragment2(item.getSubject());
                Navigation.findNavController(v).navigate(R.id.subjectDetailFragment, action.getArguments());
            }

            @javax.annotation.Nullable
            @Override
            public View onBind(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof SubjectItem.ViewHolder) {
                    return ((SubjectItem.ViewHolder) viewHolder).cardView;
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

        itemAdapter.setNewList(items);
        recyclerView.scrollToPosition(0);
    }

    private void setLayout(boolean empty) {

        int errorTvVis;
        int animVis = errorTvVis = (empty) ? View.VISIBLE : View.GONE;
        int recyclerVis = (!empty) ? View.VISIBLE : View.GONE;
        errorTextView.setVisibility(errorTvVis);
        errorTextView.setText(R.string.no_content_message);
        animationView.setVisibility(animVis);
        recyclerView.setVisibility(recyclerVis);
        if (empty) {
            animationView.playAnimation();
        }

    }

    @OnClick(R.id.settings)
    public void openSettings(View v) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        // TODO: change for navigation
    }

    @OnClick(R.id.logoutButton)
    public void logout(View v) {
        subjectsViewModel.logout();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}
