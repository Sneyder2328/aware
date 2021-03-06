package com.americanminion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Technovibe on 17-04-2015.
 */
class ChatAdapter extends BaseAdapter implements Constants {

    private final List<ChatMessage> chatMessages;
    private Activity context;
    private Handler mHandler;
    SharedPreferences sharedPreferences;
    public String API_LINK;


    ChatAdapter(Activity context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        mHandler = new Handler(Looper.getMainLooper());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        API_LINK = sharedPreferences.getString(API_LINK_TEXT, "");
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final ChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.abc, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.txtMessage.setText(chatMessage.getMessage());
        holder.timeStamp.setText(chatMessage.getDate());
        holder.txtAuthor.setText(chatMessage.getAuthor());
        holder.txtVote.setText(chatMessage.getVotes());

        String x = chatMessage.getTags();

        if (x != null) {


            Log.e("message", x + " ");

            if (x.contains("1"))
                holder.tag1.setVisibility(View.VISIBLE);
            else
                holder.tag1.setVisibility(View.GONE);
            if (x.contains("2"))
                holder.tag2.setVisibility(View.VISIBLE);
            else
                holder.tag1.setVisibility(View.GONE);
            if (x.contains("3"))
                holder.tag3.setVisibility(View.VISIBLE);
            else
                holder.tag1.setVisibility(View.GONE);
            if (x.contains("4"))
                holder.tag4.setVisibility(View.VISIBLE);
            else
                holder.tag1.setVisibility(View.GONE);
            if (x.contains("5"))
                holder.tag5.setVisibility(View.VISIBLE);
            else
                holder.tag1.setVisibility(View.GONE);
        }

        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote("-1", Long.toString(chatMessage.getId()), holder.downvote, holder.txtVote);
            }
        });

        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote("1", Long.toString(chatMessage.getId()), holder.upvote, holder.txtVote);
            }
        });

        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote("2", Long.toString(chatMessage.getId()), holder.favourite, holder.txtVote);
            }
        });


        return convertView;
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private ViewHolder createViewHolder(final View v) {
        ViewHolder holder = new ViewHolder();

        holder.txtMessage = (TextView) v.findViewById(R.id.postText);
        holder.txtAuthor = (TextView) v.findViewById(R.id.handleText);

        holder.txtVote = (TextView) v.findViewById(R.id.voteCount);
        holder.downvote = (Button) v.findViewById(R.id.downvote);
        holder.upvote = (Button) v.findViewById(R.id.upvote);
        holder.timeStamp = (TextView) v.findViewById(R.id.timestamp);
        holder.favourite = (Button) v.findViewById(R.id.favorite);

        holder.tag1 = (ImageView) v.findViewById(R.id.tag1);
        holder.tag2 = (ImageView) v.findViewById(R.id.tag2);
        holder.tag3 = (ImageView) v.findViewById(R.id.tag3);
        holder.tag4 = (ImageView) v.findViewById(R.id.tag4);
        holder.tag5 = (ImageView) v.findViewById(R.id.tag5);


        return holder;
    }


    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtAuthor;
        public TextView txtVote, timeStamp;
        Button upvote, downvote, favourite;
        ImageView tag1, tag2, tag3, tag4, tag5;

    }


    /**
     * Upload data to server
     */
    public void vote(final String score, String msgId, final Button button, final TextView textView) {

        // to fetch city names
        String uri = API_LINK + "message-vote/" + msgId + "/" + score;
        Log.e("CALLING : ", uri);

        //Set up client
        OkHttpClient client = new OkHttpClient();
        //Execute request
        final Request request = new Request.Builder()
                .url(uri)
                .build();

        //Setup callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Request Failed", "Message : " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        Integer num = Integer.parseInt(textView.getText().toString());
                        if (score.equals("1")) {
                            num++;
                            button.setBackgroundResource(R.drawable.vote_up_active);
                            textView.setText(Integer.toString(num));

                        } else if (score.equals("-1")) {
                            num--;
                            button.setBackgroundResource(R.drawable.vote_down_active);
                            textView.setText(Integer.toString(num));

                        } else {

                            button.setBackgroundResource(R.drawable.favorite_icon_active);
                        }

                    }
                });
            }
        });
    }
}
