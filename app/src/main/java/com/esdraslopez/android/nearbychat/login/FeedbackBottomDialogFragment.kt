package com.esdraslopez.android.nearbychat.login

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.esdraslopez.android.nearbychat.BuildConfig
import com.esdraslopez.android.nearbychat.R
import com.esdraslopez.android.nearbychat.Util
import kotlinx.android.synthetic.main.layout_feedback_bottom_sheet.view.*

class FeedbackBottomDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_feedback_bottom_sheet, container,
                false)

        view.play_store_review_button.setOnClickListener {
            val packageName = context?.packageName
            val playStoreIntent = Intent(Intent.ACTION_VIEW)
            playStoreIntent.data = Uri.parse("market://details?id=$packageName")

            try {
                startActivity(playStoreIntent)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
            }
        }

        view.github_issue_button.setOnClickListener { Util.goToURL(activity, "https://github.com/EzraLopez/nearbychat/issues/new") }

        view.contact_developer_button.setOnClickListener {
            val email = "androidapps@esdraslopez.com"
            val subject = getString(R.string.app_name) + " vc" + BuildConfig.VERSION_CODE + " " + getString(R.string.feedback_email_subject_ending)
            val message = "**** " + getString(R.string.feedback_email_placeholder) + "****\n\n\n"

            composeEmail(email, subject, message)
        }

        return view
    }

    private fun composeEmail(email: String, subject: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:$email"))
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, message)

        if (intent.resolveActivity(context?.packageManager) != null)
            startActivity(intent)
        else
            Toast.makeText(activity, getString(R.string.email_app_not_found_error), Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(): FeedbackBottomDialogFragment {
            return FeedbackBottomDialogFragment()
        }
    }
}
