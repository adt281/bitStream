package com.example.musicstreamproject2;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicstreamproject2.adapter.SongListAdapter;

import java.util.ArrayList;

public class SongsListActivity extends AppCompatActivity {

    ImageView coverImageView;
    TextView nameTextView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_songs_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_songs_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🔗 Link Views
        coverImageView = findViewById(R.id.cover_image_view);
        nameTextView = findViewById(R.id.name_text_view);
        recyclerView = findViewById(R.id.songs_list_recycler_view);

        // 📥 Get Data from Intent
        String name = getIntent().getStringExtra("category_name");
        String coverURL = getIntent().getStringExtra("category_coverURL");
        ArrayList<Integer> songs = getIntent().getIntegerArrayListExtra("category_songs");
        //Log.d for this list:
        Log.d("InsideSongsListActivity", ""+songs);

        // 🖼️ Load Image using Glide
        Glide.with(this)
                .load(coverURL)
                .thumbnail(
                        Glide.with(this)
                                .load(coverURL)
                                .sizeMultiplier(0.01f)
                )
                .sizeMultiplier(0.8f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(coverImageView);

        // 📝 Set name
        nameTextView.setText(name);

        // 🔁 Setup RecyclerView
        SongListAdapter adapter = new SongListAdapter(songs,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

}
