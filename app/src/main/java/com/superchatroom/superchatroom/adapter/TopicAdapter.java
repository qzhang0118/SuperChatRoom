package com.superchatroom.superchatroom.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.superchatroom.superchatroom.R;
import com.superchatroom.superchatroom.item.Topic;

import java.util.List;

/**
 * Adapter stores a list of topics.
 */
public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{
    private List<Topic> topicList;
    private ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(Topic topic);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        private View container;
        private TextView topicTextView;

        public TopicViewHolder(View view) {
            super(view);
            container = view;
            this.topicTextView = view.findViewById(R.id.topic_text_view);
        }
    }

    public TopicAdapter(List<Topic> topicList) {
        this.topicList = topicList;
    }

    public void refreshTopics(List<Topic> topics) {
        this.topicList = topics;
        notifyDataSetChanged();
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.topic_view, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, final int position) {
        Topic topic = topicList.get(position);
        holder.topicTextView.setText(topic.getDisplayName());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(topicList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }
}
