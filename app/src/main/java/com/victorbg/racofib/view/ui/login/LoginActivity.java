package com.victorbg.racofib.view.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.victorbg.racofib.R;
import com.victorbg.racofib.data.DataRepository;
import com.victorbg.racofib.data.api.result.ApiResult;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.MainActivity;
import com.victorbg.racofib.view.base.BaseActivity;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements Injectable {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Inject
    DataRepository dataRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent().getData() != null && getIntent().getDataString().startsWith("apifib://login")) {
            progressBar.setVisibility(View.VISIBLE);
            String goodResponse = getIntent().getDataString().replace("apifib://login#", "apifib://login?");
            Uri loginData = Uri.parse(goodResponse);
            String token = loginData.getQueryParameter("access_token");
            long expirationTime = Long.parseLong(loginData.getQueryParameter("expires_in"));

            //TODO: this should be in a VM
            dataRepository.loginUser(token, expirationTime, new ApiResult() {
                @Override
                public void onCompleted() {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }

                @Override
                public void onFailed(String errorMessage) {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    }

    @OnClick(R.id.login_button)
    public void login(View v) {
        String url = "https://api.fib.upc.edu/v2/o/authorize/?client_id=dzHij8jTq4tpH9EzmNgmh3svKbRwBkV54cGr3RVh&redirect_uri=apifib://login&response_type=token&state=random_state_string";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }
}
