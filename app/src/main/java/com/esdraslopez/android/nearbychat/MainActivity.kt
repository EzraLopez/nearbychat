package com.esdraslopez.android.nearbychat

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.esdraslopez.android.nearbychat.login.LoginActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "MainActivity"
    }

    private var loginTime = System.currentTimeMillis()
    private lateinit var userUUID: String
    private lateinit var messageListener: MessageListener
    private lateinit var messageListAdapter: MessageListAdapter
    private lateinit var logoutDialog: AlertDialog.Builder
    private lateinit var activeMessage: Message

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = intent.getStringExtra(LoginActivity.KEY_USERNAME)
        userUUID = intent.getStringExtra(LoginActivity.KEY_USER_UUID)

        messageListAdapter = MessageListAdapter(this, userUUID)
        messageListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                updateEmptyView()
            }

            override fun onChanged() {
                updateEmptyView()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                updateEmptyView()

                message_list_recycler.post { message_list_recycler.smoothScrollToPosition(messageListAdapter.itemCount - 1) }
            }

            private fun updateEmptyView() {
                val showEmptyView = messageListAdapter.itemCount == 0
                empty_view.visibility = if (showEmptyView) View.VISIBLE else View.GONE
            }
        })

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        message_list_recycler.layoutManager = layoutManager
        message_list_recycler.adapter = messageListAdapter

        messageListener = object : MessageListener() {
            override fun onFound(message: Message) {
                Log.d(TAG, "Found message: ${message.content}")
                val deviceMessage = DeviceMessage.fromNearbyMessage(message)
                if (deviceMessage.creationTime < loginTime) {
                    Log.d(TAG, "Found message was sent before we logged in. Won't add it to chat history.")
                } else {
                    messageListAdapter.add(deviceMessage)
                }
            }

            override fun onLost(message: Message) {
                Log.d(TAG, "Lost sight of message: ${message.content}")
            }
        }

        logoutDialog = AlertDialog.Builder(this)
        logoutDialog
                .setTitle("Are you sure you want to leave?")
                .setMessage("Your chat history will be deleted.")
                .setNegativeButton("No", null)

        send_message_button.setOnClickListener {
            val message = message_input.text.toString().trim { it <= ' ' }
            if (!message.isEmpty()) {
                val timestamp = System.currentTimeMillis()

                val deviceMessage = DeviceMessage(userUUID, username, message, timestamp)

                activeMessage = deviceMessage.message
                Log.d(TAG, "Publishing message = ${activeMessage.content}")
                Nearby.getMessagesClient(this).publish(activeMessage)

                messageListAdapter.add(deviceMessage)
                message_input.setText("")
            }
        }
    }

    public override fun onStart() {
        super.onStart()

        Nearby.getMessagesClient(this).subscribe(messageListener)
    }

    public override fun onStop() {
        if (::activeMessage.isInitialized)
            Nearby.getMessagesClient(this).unpublish(activeMessage)

        Nearby.getMessagesClient(this).unsubscribe(messageListener)

        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutDialog.setPositiveButton("Yes") { _, _ ->
                    Util.clearSharedPreferences(this@MainActivity)
                    finish()
                }.show()
                true
            }
            R.id.action_clear_chat_history -> {
                messageListAdapter.clear()
                loginTime = System.currentTimeMillis()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        logoutDialog.setPositiveButton("Yes") { _, _ ->
            Util.clearSharedPreferences(this@MainActivity)
            super@MainActivity.onBackPressed()
        }.show()
    }
}
