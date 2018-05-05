package com.esdraslopez.android.nearbychat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.esdraslopez.android.nearbychat.login.LoginActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String username;
    private String userUUID;
    private long loginTime;

    private MessageListener messageListener;
    private Message activeMessage;

    private AlertDialog.Builder logoutDialog;

    private MessageListAdapter messageListAdapter;

    @BindView(android.R.id.content) ViewGroup container;
    @BindView(R.id.message_input) EditText messageInput;
    @BindView(R.id.message_list_recycler) RecyclerView messageListRecycler;
    @BindView(R.id.empty_view) Group chatHistoryEmptyView;

    @OnClick(R.id.send_message_button)
    public void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            long timestamp = System.currentTimeMillis();

            DeviceMessage deviceMessage = new DeviceMessage(userUUID, username, message, timestamp);

            activeMessage = deviceMessage.getMessage();
            Log.d(TAG, "Publishing message = " + new String(activeMessage.getContent()));
            Nearby.getMessagesClient(this).publish(activeMessage);

            messageListAdapter.add(deviceMessage);
            messageInput.setText("");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        username = getIntent().getStringExtra(LoginActivity.KEY_USERNAME);
        userUUID = getIntent().getStringExtra(LoginActivity.KEY_USER_UUID);

        loginTime = System.currentTimeMillis();

        messageListAdapter = new MessageListAdapter(this, userUUID);
        messageListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateEmptyView();
            }

            @Override
            public void onChanged() {
                updateEmptyView();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                updateEmptyView();

                messageListRecycler.post(new Runnable() {
                    @Override
                    public void run() {
                        messageListRecycler.smoothScrollToPosition(messageListAdapter.getItemCount() - 1);
                    }
                });
            }

            private void updateEmptyView() {
                boolean showEmptyView = messageListAdapter.getItemCount() == 0;
                chatHistoryEmptyView.setVisibility(showEmptyView ? View.VISIBLE : View.GONE);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messageListRecycler.setLayoutManager(layoutManager);
        messageListRecycler.setAdapter(messageListAdapter);

        messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d(TAG, "Found message: " + new String(message.getContent()));
                DeviceMessage deviceMessage = DeviceMessage.Companion.fromNearbyMessage(message);
                if (deviceMessage.getCreationTime() < loginTime) {
                    Log.d(TAG, "Found message was sent before we logged in. Won't add it to chat history.");
                } else {
                    messageListAdapter.add(deviceMessage);
                }
            }

            @Override
            public void onLost(Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };

        logoutDialog = new AlertDialog.Builder(this);
        logoutDialog
                .setTitle("Are you sure you want to leave?")
                .setMessage("Your chat history will be deleted.")
                .setNegativeButton("No", null);
    }

    @Override
    public void onStart() {
        super.onStart();

        Nearby.getMessagesClient(this).subscribe(messageListener);
    }

    @Override
    public void onStop() {
        if (activeMessage != null)
            Nearby.getMessagesClient(this).unpublish(activeMessage);

        Nearby.getMessagesClient(this).unsubscribe(messageListener);

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logoutDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.clearSharedPreferences(MainActivity.this);
                        finish();
                    }
                }).show();
                return true;
            case R.id.action_clear_chat_history:
                messageListAdapter.clear();
                loginTime = System.currentTimeMillis();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        logoutDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Util.clearSharedPreferences(MainActivity.this);
                MainActivity.super.onBackPressed();
            }
        }).show();
    }
}
