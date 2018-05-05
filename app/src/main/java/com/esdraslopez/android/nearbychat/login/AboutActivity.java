package com.esdraslopez.android.nearbychat.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.esdraslopez.android.nearbychat.BuildConfig;
import com.esdraslopez.android.nearbychat.R;
import com.esdraslopez.android.nearbychat.Util;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.app_version) TextView appVersion;

    @OnClick({R.id.dev_website_button, R.id.dev_github_button, R.id.dev_twitter_button, R.id.dev_linkedin_button,
            R.id.designer_linkedin_button,
            R.id.source_code_button})
    public void openWebsite(Button button) {
        String url = "";
        switch (button.getId()) {
            case R.id.dev_website_button:
                url = "https://esdraslopez.com";
                break;
            case R.id.dev_github_button:
                url = "https://github.com/ezralopez";
                break;
            case R.id.dev_twitter_button:
                url = "https://twitter.com/ezranlopez";
                break;
            case R.id.dev_linkedin_button:
                url = "https://linkedin.com/in/ezralopez/";
                break;
            case R.id.designer_linkedin_button:
                url = "https://linkedin.com/in/kristinlopez/";
                break;
            case R.id.source_code_button:
                url = "https://github.com/EzraLopez/nearbychat";
                break;
        }

        Util.goToURL(this, url);
    }

    @OnClick(R.id.licenses_button)
    public void openLicensesFragment() {
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTitle(getString(R.string.licenses_activity_label))
                .start(this);
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        appVersion.setText("v" + BuildConfig.VERSION_NAME);
    }
}
