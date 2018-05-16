package com.esdraslopez.android.nearbychat.login

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.core.content.edit
import com.esdraslopez.android.nearbychat.MainActivity
import com.esdraslopez.android.nearbychat.R
import com.esdraslopez.android.nearbychat.Util
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "LoginActivity"
        const val KEY_USERNAME = "username"
        const val KEY_USER_UUID = "user-uuid"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Util.clearSharedPreferences(this)

        username_input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
                true
            } else {
                false
            }
        }

        login_button.setOnClickListener { login() }
        about_button.setOnClickListener { Util.startActivity(this, AboutActivity::class.java) }
        feedback_button.setOnClickListener { FeedbackBottomDialogFragment.newInstance().show(supportFragmentManager, "feedback_fragment") }
    }

    fun login() {
        login_button.clearFocus()
        login_button.isEnabled = false
        Util.hideKeyboard(this)

        if (Util.isConnected(this@LoginActivity)) {

            var username = username_input.text.toString()
            if (username.isEmpty()) username = "Anonymous"

            val userUUID = UUID.randomUUID().toString()

            Util.getSharedPreferences(this).edit {
                putString(KEY_USER_UUID, userUUID)
                putString(KEY_USERNAME, username)
            }

            Log.i(TAG, "Logging in user.")
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(KEY_USERNAME, username)
                    .putExtra(KEY_USER_UUID, userUUID)
            startActivity(intent)
        } else
            Snackbar.make(container, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()

        login_button.isEnabled = true
    }
}
