package com.superchatroom.superchatroom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Adaptor stores a list of messages.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static final int LEFT_MESSAGE_VIEW = 0;
    private static final int RIGHT_MESSAGE_VIEW = 1;

    protected static class MessageViewHolder extends RecyclerView.ViewHolder {
        protected TextView timeTextView;
        protected TextView messageTextView;

        public MessageViewHolder(View view) {
            super(view);
            this.timeTextView = view.findViewById(R.id.time);
            this.messageTextView = view.findViewById(R.id.message);
        }
    }

    private List<MessageItem> messageItems;

    public MessageAdapter(List<MessageItem> messageItems) {
        this.messageItems = messageItems;
    }

    /**
     * Appends a message to the end of the list of messages.
     */
    public void appendMessageItem(MessageItem messageItem) {
        messageItems.add(messageItem);
        notifyItemInserted(getItemCount());
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(viewType == LEFT_MESSAGE_VIEW ?
                                R.layout.left_message_view : R.layout.right_message_view,
                        parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        MessageItem messageItem = messageItems.get(position);
        holder.timeTextView.setText(messageItem.getReceivedTime());
        String receivedMessage = messageItem.getMessage();
        int index = receivedMessage.indexOf(":");
        holder.messageTextView.setText(receivedMessage.substring(0, index) + ": "
                + receivedMessage.substring(index + 1));
    }

    @Override
    public int getItemViewType(int position) {
        MessageItem messageItem = messageItems.get(position);
        String receivedMessage = messageItem.getMessage();
        int index = receivedMessage.indexOf(":");
        if (receivedMessage.substring(0, index)
                .equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
            return RIGHT_MESSAGE_VIEW;
        }
        return LEFT_MESSAGE_VIEW;
    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }
}
