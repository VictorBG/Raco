package com.victorbg.racofib.view.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.victorbg.racofib.R;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.MainActivity;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.viewmodel.LoginViewModel;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements Injectable {

  private static final String STATE = "PiyiidVvcoywpoAeHUtMUESuwekIVBpFZMWPSmwq";

  @BindView(R.id.progressBar)
  ProgressBar progressBar;

  @BindView(R.id.status_message)
  TextView statusMessage;

  @BindView(R.id.parent)
  View parent;

  private LoginViewModel loginViewModel;

  @Inject ViewModelProvider.Factory viewModelFactory;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    parent.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (getIntent().getDataString() != null
        && getIntent().getDataString().startsWith("apifib://login")) {
      loginViewModel.login(getIntent().getData(), STATE).observe(this, this::handleLoginState);
    }
  }

  private void handleLoginState(Resource<String> state) {
    switch (state.status) {
      case LOADING:
        progressBar.setVisibility(View.VISIBLE);
        statusMessage.setVisibility(View.VISIBLE);
        statusMessage.setText(state.data);
        break;
      case ERROR:
        progressBar.setVisibility(View.GONE);
        statusMessage.setVisibility(View.VISIBLE);
        statusMessage.setText(R.string.general_error);
        break;
      case SUCCESS:
        startActivity(new Intent(this, MainActivity.class));
        finish();
        break;
    }
  }

  @OnClick(R.id.login_button)
  public void login(View v) {
    String url =
        "https://api.fib.upc.edu/v2/o/authorize/?client_id=dzHij8jTq4tpH9EzmNgmh3svKbRwBkV54cGr3RVh&redirect_uri=apifib://login&response_type=code&state="
            + STATE;
    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    CustomTabsIntent customTabsIntent = builder.build();
    customTabsIntent.launchUrl(this, Uri.parse(url));
  }

  protected int getLightTheme() {
    return R.style.AppTheme_Login;
  }

  protected int getDarkTheme() {
    return R.style.AppTheme_Login;
  }
}
