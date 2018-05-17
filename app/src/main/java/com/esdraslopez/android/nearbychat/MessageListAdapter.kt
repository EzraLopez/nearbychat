package com.esdraslopez.android.nearbychat

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import android.widget.Toast
import java.util.*

@Suppress("unused")
class MessageListAdapter internal constructor(private val activity: Activity, private val loggedInUserUUID: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    private val messageList: SortedList<DeviceMessage>
    private var selectedPosition: Int = 0
    private var pressedPosition: Int = 0
    private lateinit var recyclerView: RecyclerView

    init {
        messageList = SortedList(DeviceMessage::class.java, object : SortedList.Callback<DeviceMessage>() {
            override fun compare(o1: DeviceMessage, o2: DeviceMessage): Int {
                val creationTimeResult = java.lang.Long.compare(o1.creationTime, o2.creationTime)
                return if (creationTimeResult != 0) creationTimeResult else o1.userUUID.compareTo(o2.userUUID)

            }

            override fun onChanged(position: Int, count: Int) {
                notifyItemRangeChanged(position, count)
            }

            override fun areContentsTheSame(oldItem: DeviceMessage, newItem: DeviceMessage): Boolean {
                return oldItem.message == newItem.message
            }

            override fun areItemsTheSame(item1: DeviceMessage, item2: DeviceMessage): Boolean {
                return item1.creationTime == item2.creationTime && item1.userUUID == item2.userUUID
            }

            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }
        })
        this.selectedPosition = RecyclerView.NO_POSITION
        this.pressedPosition = RecyclerView.NO_POSITION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

        when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_sent, parent, false)
                return SentMessageHolder(view)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_received, parent, false)
                return ReceivedMessageHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
                return ReceivedMessageHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size()
    }

    override fun getItemViewType(position: Int): Int {
        val senderUserUUID = messageList.get(position).userUUID

        return if (senderUserUUID == loggedInUserUUID) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList.get(position)

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    operator fun get(position: Int): DeviceMessage {
        return messageList.get(position)
    }

    fun add(item: DeviceMessage): Int {
        return messageList.add(item)
    }

    fun indexOf(item: DeviceMessage): Int {
        return messageList.indexOf(item)
    }

    fun updateItemAt(index: Int, item: DeviceMessage) {
        messageList.updateItemAt(index, item)
    }

    private fun addAll(items: List<DeviceMessage>) {
        messageList.beginBatchedUpdates()
        for (item in items) {
            messageList.add(item)
        }
        messageList.endBatchedUpdates()
    }

    fun addAll(items: Array<DeviceMessage>) {
        addAll(Arrays.asList(*items))
    }

    fun remove(item: DeviceMessage): Boolean {
        return messageList.remove(item)
    }

    fun removeItemAt(index: Int): DeviceMessage {
        return messageList.removeItemAt(index)
    }

    fun clear() {
        messageList.beginBatchedUpdates()
        while (messageList.size() > 0) {
            messageList.removeItemAt(messageList.size() - 1)
        }
        messageList.endBatchedUpdates()
    }

    internal abstract inner class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var actionMode: ActionMode? = null
        var messageBody: TextView
        var messageCreationTime: TextView

        init {
            if (this is SentMessageHolder) {
                messageBody = itemView.findViewById(R.id.sent_message_body)
                messageCreationTime = itemView.findViewById(R.id.sent_message_creation_time)
            } else {
                messageBody = itemView.findViewById(R.id.received_message_body)
                messageCreationTime = itemView.findViewById(R.id.received_message_creation_time)
            }
        }

        private val actionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.context_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.menu_share -> {
                        shareMessage(messageBody.text.toString())
                        mode.finish()
                        true
                    }
                    R.id.menu_copy -> {
                        copyMessageToClipboard(messageBody.text.toString())
                        mode.finish()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                actionMode = null
                looseFocus()
                pressedPosition = RecyclerView.NO_POSITION
            }
        }

        internal open fun bind(message: DeviceMessage) {
            messageBody.text = message.messageBody
            messageCreationTime.text = Util.formatDateTime(message.creationTime)

            if (selectedPosition != RecyclerView.NO_POSITION && adapterPosition == selectedPosition)
                showCreationTime()
            else
                hideCreationTime()

            messageBody.setOnClickListener {
                if (pressedPosition != RecyclerView.NO_POSITION) {
                    val pressedView = recyclerView.findViewHolderForAdapterPosition(pressedPosition) as MessageHolder?
                    if (pressedView != null) {
                        pressedView.actionMode?.finish()
                    }
                }

                selectedPosition = if (messageCreationTime.visibility == View.VISIBLE) {
                    hideCreationTime()
                    RecyclerView.NO_POSITION
                } else {
                    hideSelectedView()
                    showCreationTime()
                    adapterPosition
                }
            }

            messageBody.setOnLongClickListener(View.OnLongClickListener {
                if (actionMode != null) {
                    return@OnLongClickListener false
                }
                actionMode = activity.startActionMode(actionModeCallback)
                hideSelectedView()
                pressedPosition = adapterPosition
                focus()
                true
            })
        }

        internal abstract fun showCreationTime()
        internal abstract fun hideCreationTime()
        internal abstract fun focus()
        internal abstract fun looseFocus()
    }

    internal inner class SentMessageHolder(itemView: View) : MessageHolder(itemView) {

        override fun showCreationTime() {
            messageCreationTime.visibility = View.VISIBLE
            focus()
        }

        override fun hideCreationTime() {
            messageCreationTime.visibility = View.INVISIBLE
            looseFocus()
        }

        override fun focus() {
            messageBody.background = ResourcesCompat.getDrawable(itemView.resources, R.drawable.rounded_rectangle_primary_dark_color, null)
        }

        override fun looseFocus() {
            messageBody.background = ResourcesCompat.getDrawable(itemView.resources, R.drawable.rounded_rectangle_primary_color, null)
        }
    }

    internal inner class ReceivedMessageHolder(itemView: View) : MessageHolder(itemView) {

        private var messageAuthor: TextView = itemView.findViewById(R.id.received_message_author)

        override fun bind(message: DeviceMessage) {
            super.bind(message)

            // Group consecutive messages from the same sender
            val currentPos = this.adapterPosition
            if (currentPos > 0 && messageList.get(currentPos - 1).userUUID == message.userUUID) {
                messageAuthor.visibility = View.GONE
            } else {
                messageAuthor.visibility = View.VISIBLE
                messageAuthor.text = message.username
            }
        }

        override fun showCreationTime() {
            messageCreationTime.visibility = View.VISIBLE
            focus()
        }

        override fun hideCreationTime() {
            messageCreationTime.visibility = View.INVISIBLE
            looseFocus()
        }

        override fun focus() {
            messageBody.background = ResourcesCompat.getDrawable(itemView.resources, R.drawable.rounded_rectangle_dark_gray, null)
        }

        override fun looseFocus() {
            messageBody.background = ResourcesCompat.getDrawable(itemView.resources, R.drawable.rounded_rectangle_light_gray, null)
        }
    }

    fun shareMessage(message: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.type = "text/plain"
        activity.startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.send_to)))
    }

    fun copyMessageToClipboard(message: String) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("message", message)
        Objects.requireNonNull(clipboard).primaryClip = clip
        Toast.makeText(activity.applicationContext, R.string.message_copied_notification, Toast.LENGTH_SHORT).show()
    }

    fun hideSelectedView() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            val selectedView = recyclerView.findViewHolderForAdapterPosition(selectedPosition) as MessageHolder
            selectedView.hideCreationTime()
        }
    }
}