package com.example.neurochatai;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neurochatai.adapter.ChatAdapter;
import com.example.neurochatai.adapter.HistoryAdapter;
import com.example.neurochatai.model.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView, historyRecycler;
    EditText editText;
    ImageView sendBtn, menuBtn;
    Button newChatBtn;
    View sidebar, emptyMessage;

    List<Message> messageList = new ArrayList<>();
    List<List<Message>> allChats = new ArrayList<>();
    List<String> titles = new ArrayList<>();

    ChatAdapter adapter;
    HistoryAdapter historyAdapter;

    OkHttpClient client = new OkHttpClient();

    String API_KEY = "api_key_here";

    int currentChat = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setDecorFitsSystemWindows(true);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        historyRecycler = findViewById(R.id.historyRecycler);
        editText = findViewById(R.id.editText);
        sendBtn = findViewById(R.id.sendBtn);
        menuBtn = findViewById(R.id.menuBtn);
        newChatBtn = findViewById(R.id.newChatBtn);
        sidebar = findViewById(R.id.sidebar);
        emptyMessage = findViewById(R.id.emptyMessage);

        adapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        historyAdapter = new HistoryAdapter(titles, position -> {
            messageList.clear();
            messageList.addAll(allChats.get(position));
            adapter.notifyDataSetChanged();
            sidebar.setVisibility(View.GONE);
            currentChat = position;
            updateEmptyState(); //  update empty state
        });

        historyRecycler.setLayoutManager(new LinearLayoutManager(this));
        historyRecycler.setAdapter(historyAdapter);

        // SIDEBAR ANIMATION
        menuBtn.setOnClickListener(v -> {
            if (sidebar.getVisibility() == View.GONE) {
                sidebar.setTranslationX(-300f);
                sidebar.setVisibility(View.VISIBLE);
                sidebar.animate().translationX(0f).setDuration(300);
            } else {
                sidebar.animate().translationX(-300f).setDuration(300)
                        .withEndAction(() -> sidebar.setVisibility(View.GONE));
            }
        });

        // NEW CHAT
        newChatBtn.setOnClickListener(v -> {
            messageList.clear();
            adapter.notifyDataSetChanged();
            currentChat = -1;
            sidebar.setVisibility(View.GONE);
            updateEmptyState(); //  show empty message again
        });

        sendBtn.setOnClickListener(v -> sendMessage());

        updateEmptyState(); // initial state
    }

    void sendMessage() {
        String text = editText.getText().toString().trim();

        if (!text.isEmpty()) {

            hideEmptyState(); //  hide welcome message

            messageList.add(new Message(text, "user"));
            adapter.notifyDataSetChanged();

            if (currentChat == -1) {
                allChats.add(new ArrayList<>(messageList));
                titles.add(text.length() > 20 ? text.substring(0, 20) : text);
                historyAdapter.notifyDataSetChanged();
                currentChat = allChats.size() - 1;
            }

            editText.setText("");

            messageList.add(new Message("● ● ●", "bot"));
            adapter.notifyDataSetChanged();

            callAPI(text);
        }
    }

    void hideEmptyState() {
        if (emptyMessage.getVisibility() == View.VISIBLE) {
            emptyMessage.animate().alpha(0f).setDuration(300)
                    .withEndAction(() -> emptyMessage.setVisibility(View.GONE));
        }
    }

    void updateEmptyState() {
        if (messageList.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            emptyMessage.setAlpha(1f);
        } else {
            emptyMessage.setVisibility(View.GONE);
        }
    }

    void callAPI(String msg) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        try {
            JSONObject json = new JSONObject();
            json.put("model", "llama-3.1-8b-instant");

            JSONArray arr = new JSONArray();
            JSONObject user = new JSONObject();
            user.put("role", "user");
            user.put("content", msg);
            arr.put(user);

            json.put("messages", arr);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {}

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String res = response.body().string();
                    String reply = "Error";

                    try {
                        JSONObject obj = new JSONObject(res);
                        reply = obj.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                    } catch (Exception e) {}

                    String finalReply = reply;

                    runOnUiThread(() -> {

                        messageList.remove(messageList.size() - 1);
                        messageList.add(new Message(finalReply, "bot"));
                        adapter.notifyDataSetChanged();

                        if (currentChat != -1) {
                            allChats.set(currentChat, new ArrayList<>(messageList));
                        }

                        recyclerView.smoothScrollToPosition(messageList.size() - 1);
                    });
                }
            });

        } catch (Exception e) {}
    }
}