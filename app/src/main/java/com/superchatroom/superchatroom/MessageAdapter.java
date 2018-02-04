package com.superchatroom.superchatroom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Adaptor stores a list of messages.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    protected static class MessageViewHolder extends RecyclerView.ViewHolder {
        protected TextView timeTextView;
        protected TextView messageTextView;

        public MessageViewHolder(View view) {
            super(view);
            this.timeTextView = (TextView) view.findViewById(R.id.time);
            this.messageTextView = (TextView) view.findViewById(R.id.message);
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
                inflate(R.layout.message_view, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        MessageItem messageItem = messageItems.get(position);
        holder.timeTextView.setText(messageItem.getReceivedTime());
        holder.messageTextView.setText(messageItem.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }
}
