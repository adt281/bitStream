package com.example.musicstreamproject2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicstreamproject2.adapter.SongListAdapter;
import com.example.musicstreamproject2.models.SongModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SongsListActivity extends BaseActivity {

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
        RequestBuilder<Drawable> thumbnailRequest = Glide.with(this)
                .load(coverURL)
                .sizeMultiplier(0.01f); // tiny thumbnail

        Glide.with(this)
                .load(coverURL)
                .thumbnail(thumbnailRequest) // ✅ RequestBuilder with multiplier
                .sizeMultiplier(0.8f)        // ✅ final image size 80%
                .diskCacheStrategy(DiskCacheStrategy.ALL) // optional
                .into(coverImageView);


        // 📝 Set name
        nameTextView.setText(name);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //TODO: using id's here itself obtain the SongModel list of song metadata using wherein() using id's
        if (songs != null && !songs.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            List<String> stringIds = songs.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            db.collection("songs")
                    .whereIn(FieldPath.documentId(), stringIds.subList(0, Math.min(10, stringIds.size())))
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<SongModel> songsList = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot) {
                            SongModel model = doc.toObject(SongModel.class);
                            if (model != null) {
                                songsList.add(model);
                            }
                        }

                        SongListAdapter adapter = new SongListAdapter((ArrayList<SongModel>) songsList, this);
                        recyclerView.setAdapter(adapter);
                    });
        } else {
            // 🧹 Graceful handling when song list is empty
            recyclerView.setAdapter(new SongListAdapter(new ArrayList<>(), this));
        }


    }

}
