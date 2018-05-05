package com.esdraslopez.android.nearbychat.login;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esdraslopez.android.nearbychat.BuildConfig;
import com.esdraslopez.android.nearbychat.R;
import com.esdraslopez.android.nearbychat.Util;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackBottomDialogFragment extends BottomSheetDialogFragment {
    public static FeedbackBottomDialogFragment newInstance() {
        return new FeedbackBottomDialogFragment();
    }

    @OnClick(R.id.play_store_review_button)
    public void writePlayStoreReview() {
        String packageName = Objects.requireNonNull(getContext()).getPackageName();
        Uri uri = Uri.parse("market://details?id=" + packageName);

        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, uri);

        // to be taken back to our application after going the play store
        // we need to add following flags to the intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playStoreIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            startActivity(playStoreIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    @OnClick(R.id.github_issue_button)
    public void createGitHubIssue() {
        Util.goToURL(Objects.requireNonNull(getContext()), "https://github.com/EzraLopez/nearbychat/issues/new");
    }

    @OnClick(R.id.contact_developer_button)
    public void contactDeveloper() {
        String email = "me@esdraslopez.com";
        String subject = getString(R.string.app_name) + " vc" + BuildConfig.VERSION_CODE + " Feedback";
        String message = "**** Remove this text to prove you are not a robot. ****\n\n\n";

        composeEmail(email, subject, message);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_feedback_bottom_sheet, container,
                false);

        ButterKnife.bind(this, view);

        return view;

    }

    private void composeEmail(String email, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:" + email))
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, message);

        if (intent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(getContext(), "Could not find an email app", Toast.LENGTH_SHORT).show();
    }
}
