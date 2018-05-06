package com.esdraslopez.android.nearbychat.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.esdraslopez.android.nearbychat.BuildConfig
import com.esdraslopez.android.nearbychat.R
import com.esdraslopez.android.nearbychat.Util
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder

import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        app_version.text = "v" + BuildConfig.VERSION_NAME

        dev_website_button.setOnClickListener { Util.goToURL(this, "https://esdraslopez.com") }
        dev_github_button.setOnClickListener { Util.goToURL(this, "https://github.com/ezralopez") }
        dev_twitter_button.setOnClickListener { Util.goToURL(this, "https://twitter.com/ezranlopez") }
        dev_linkedin_button.setOnClickListener { Util.goToURL(this, "https://linkedin.com/in/ezralopez/") }
        designer_linkedin_button.setOnClickListener { Util.goToURL(this, "https://linkedin.com/in/kristinlopez/") }
        source_code_button.setOnClickListener { Util.goToURL(this, "https://github.com/EzraLopez/nearbychat") }
        licenses_button.setOnClickListener {
            LibsBuilder()
                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                    .withActivityTitle(getString(R.string.licenses_activity_label))
                    .start(this)
        }
    }
}
