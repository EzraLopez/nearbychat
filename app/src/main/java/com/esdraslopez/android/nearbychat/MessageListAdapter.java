package com.esdraslopez.android.nearbychat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private SortedList<DeviceMessage> messageList;
    private String loggedInUserUUID;
    private int selectedPosition;
    private int pressedPosition;
    private RecyclerView recyclerView;
    private final Activity activity;

    MessageListAdapter(Activity activity, String loggedInUserUUID) {
        messageList = new SortedList<>(DeviceMessage.class, new SortedList.Callback<DeviceMessage>() {
            @Override
            public int compare(DeviceMessage o1, DeviceMessage o2) {
                int creationTimeResult = Long.compare(o1.getCreationTime(), o2.getCreationTime());
                if (creationTimeResult != 0)
                    return creationTimeResult;

                return o1.getUserUUID().compareTo(o2.getUserUUID());
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(DeviceMessage oldItem, DeviceMessage newItem) {
                return oldItem.getMessage().equals(newItem.getMessage());
            }

            @Override
            public boolean areItemsTheSame(DeviceMessage item1, DeviceMessage item2) {
                return item1.getCreationTime() == item2.getCreationTime() &&
                        item1.getUserUUID().equals(item2.getUserUUID());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
        this.loggedInUserUUID = loggedInUserUUID;
        this.selectedPosition = RecyclerView.NO_POSITION;
        this.pressedPosition = RecyclerView.NO_POSITION;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case VIEW_TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageHolder(view);
            case VIEW_TYPE_MESSAGE_RECEIVED:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String senderUserUUID = messageList.get(position).getUserUUID();

        if (senderUserUUID.equals(loggedInUserUUID)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DeviceMessage message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
    }

    public DeviceMessage get(int position) {
        return messageList.get(position);
    }

    public int add(DeviceMessage item) {
        return messageList.add(item);
    }

    public int indexOf(DeviceMessage item) {
        return messageList.indexOf(item);
    }

    public void updateItemAt(int index, DeviceMessage item) {
        messageList.updateItemAt(index, item);
    }

    public void addAll(List<DeviceMessage> items) {
        messageList.beginBatchedUpdates();
        for (DeviceMessage item : items) {
            messageList.add(item);
        }
        messageList.endBatchedUpdates();
    }

    public void addAll(DeviceMessage[] items) {
        addAll(Arrays.asList(items));
    }

    public boolean remove(DeviceMessage item) {
        return messageList.remove(item);
    }

    public DeviceMessage removeItemAt(int index) {
        return messageList.removeItemAt(index);
    }

    public void clear() {
        messageList.beginBatchedUpdates();
        while (messageList.size() > 0) {
            messageList.removeItemAt(messageList.size() - 1);
        }
        messageList.endBatchedUpdates();
    }

    abstract class MessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_message_body) TextView messageText;
        @BindView(R.id.text_message_time) TextView creationTimeText;
        ActionMode actionMode;

        MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_share:
                        shareMessage();
                        mode.finish();
                        return true;
                    case R.id.menu_copy:
                        copyMessageToClipboard();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                if (creationTimeText.getVisibility() != View.VISIBLE) looseFocus();
                pressedPosition = RecyclerView.NO_POSITION;
            }
        };

        void bind(DeviceMessage message) {
            messageText.setText(message.getMessageBody());
            creationTimeText.setText(Util.formatDateTime(message.getCreationTime()));

            if (selectedPosition != RecyclerView.NO_POSITION && getAdapterPosition() == selectedPosition)
                showCreationTime();
            else
                hideCreationTime();

            messageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(pressedPosition != RecyclerView.NO_POSITION) {
                        MessageHolder pressedView = (MessageHolder) recyclerView.findViewHolderForAdapterPosition(pressedPosition);
                        if (pressedView != null) {
                            pressedView.actionMode.finish();
                        }
                    }

                    if (getCreationTimeText().getVisibility() == View.VISIBLE) {
                        hideCreationTime();
                        selectedPosition = RecyclerView.NO_POSITION;
                    } else {
                        hideSelectedView();
                        showCreationTime();
                        selectedPosition = getAdapterPosition();
                    }
                }
            });

            messageText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (actionMode != null) {
                        return false;
                    }
                    actionMode = activity.startActionMode(actionModeCallback);
                    hideSelectedView();
                    pressedPosition = getAdapterPosition();
                    focus();
                    return true;
                }
            });
        }

        TextView getMessageText() {
            return messageText;
        }

        TextView getCreationTimeText() {
            return creationTimeText;
        }

        abstract void showCreationTime();

        abstract void hideCreationTime();

        abstract void focus();

        abstract void looseFocus();

        void shareMessage() {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getMessageText().getText());
            sendIntent.setType("text/plain");
            activity.startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.send_to)));
        }

        void copyMessageToClipboard() {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("message", MessageHolder.this.getMessageText().getText());
            Objects.requireNonNull(clipboard).setPrimaryClip(clip);
            Toast.makeText(activity.getApplicationContext(), R.string.message_copied_notification, Toast.LENGTH_SHORT).show();
        }

        void hideSelectedView() {
            if (selectedPosition != RecyclerView.NO_POSITION) {
                MessageHolder selectedView = (MessageHolder) recyclerView.findViewHolderForAdapterPosition(selectedPosition);
                if (selectedView != null) {
                    selectedView.hideCreationTime();
                }
            }
        }
    }

    class SentMessageHolder extends MessageHolder {

        SentMessageHolder(View itemView) {
            super(itemView);
        }

        @Override
        void showCreationTime() {
            getCreationTimeText().setVisibility(View.VISIBLE);
            focus();
        }

        @Override
        void hideCreationTime() {
            getCreationTimeText().setVisibility(View.INVISIBLE);
            looseFocus();
        }

        @Override
        void focus() {
            getMessageText().setBackground(itemView.getResources().getDrawable(R.drawable.rounded_rectangle_primary_dark_color));
        }

        @Override
        void looseFocus() {
            getMessageText().setBackground(itemView.getResources().getDrawable(R.drawable.rounded_rectangle_primary_color));
        }
    }

    class ReceivedMessageHolder extends MessageHolder {
        @BindView(R.id.text_message_name) TextView usernameText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
        }

        void bind(DeviceMessage message) {
            super.bind(message);

            // Group consecutive messages from the same sender
            int currentPos = this.getAdapterPosition();
            if (currentPos > 0 && messageList.get(currentPos - 1).getUserUUID().equals(message.getUserUUID())) {
                usernameText.setVisibility(View.GONE);
            } else {
                usernameText.setVisibility(View.VISIBLE);
                usernameText.setText(message.getUsername());
            }
        }

        @Override
        void showCreationTime() {
            getCreationTimeText().setVisibility(View.VISIBLE);
            focus();
        }

        @Override
        void hideCreationTime() {
            getCreationTimeText().setVisibility(View.INVISIBLE);
            looseFocus();
        }

        @Override
        void focus() {
            getMessageText().setBackground(itemView.getResources().getDrawable(R.drawable.rounded_rectangle_dark_gray));
        }

        @Override
        void looseFocus() {
            getMessageText().setBackground(itemView.getResources().getDrawable(R.drawable.rounded_rectangle_light_gray));
        }
    }
}