package com.example.musicstreamproject2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicstreamproject2.adapter.CategoryAdapter;
import com.example.musicstreamproject2.adapter.SectionSongListAdapter;
import com.example.musicstreamproject2.models.CategoryModel;
import com.example.musicstreamproject2.models.SongModel;
import com.example.musicstreamproject2.player.MiniPlayer;
import com.example.musicstreamproject2.player.MyExoPlayer;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private MiniPlayer miniPlayer;
    private FrameLayout miniPlayerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize mini player
        miniPlayerContainer = findViewById(R.id.mini_player_container);
        miniPlayer = new MiniPlayer(this);
        miniPlayerContainer.addView(miniPlayer.getView());

        //TODO: Recycler view Horizontal setup
        recyclerView = findViewById(R.id.categories_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        getCategories();

        //TODO: for sections:
        RelativeLayout section1MainLayout = findViewById(R.id.section_1_main_layout);
        TextView section1Title = findViewById(R.id.section_1_title);
        RecyclerView section1RecyclerView = findViewById(R.id.section_1_recycler_view);

        RelativeLayout section2MainLayout = findViewById(R.id.section_2_main_layout);
        TextView section2Title = findViewById(R.id.section_2_title);
        RecyclerView section2RecyclerView = findViewById(R.id.section_2_recycler_view);

        RelativeLayout section3MainLayout = findViewById(R.id.section_3_main_layout);
        TextView section3Title = findViewById(R.id.section_3_title);
        RecyclerView section3RecyclerView = findViewById(R.id.section_3_recycler_view);

        setupSection("section_1", section1MainLayout, section1Title, section1RecyclerView);
        setupSection("section_2", section2MainLayout, section2Title, section2RecyclerView);
        setupSection("section_3", section3MainLayout, section3Title, section3RecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update mini player when returning to MainActivity
        SongModel currentSong = MyExoPlayer.getCurrentSong();
        if (currentSong != null) {
            showMiniPlayer(currentSong);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (miniPlayer != null) {
            miniPlayer.destroy();
        }
    }

    public void showMiniPlayer(SongModel song) {
        if (miniPlayer != null && song != null) {
            miniPlayer.updateSong(song);
            miniPlayer.showMiniPlayer();
            miniPlayerContainer.setVisibility(View.VISIBLE);
        }
    }

    public void hideMiniPlayer() {
        if (miniPlayer != null) {
            miniPlayer.hideMiniPlayer();
            miniPlayerContainer.setVisibility(View.GONE);
        }
    }

    void getCategories() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // ✅ Enable cache if not already set
        //TODO: TO BE ENABLED THE FIRST TIME YOU EVER USE FIREBASE IN YOUR APP, SETTED FOR ALL USES
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();

        db.setFirestoreSettings(settings);

        db.collection("category")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CategoryModel> categoryList = queryDocumentSnapshots.toObjects(CategoryModel.class);
                    setupCategoryRecyclerView(categoryList);
                })  .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error fetching categories", e);
                });
    }

    void setupCategoryRecyclerView(List<CategoryModel> categoryList) {

        CategoryAdapter categoryAdapter=new CategoryAdapter((ArrayList<CategoryModel>) categoryList,this);
        recyclerView.setAdapter(categoryAdapter);

    }
    private void setupSection(String id, RelativeLayout mainLayout, TextView titleView, RecyclerView recyclerView) {
        FirebaseFirestore.getInstance().collection("sections")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    CategoryModel section = documentSnapshot.toObject(CategoryModel.class);
                    if (section != null && section.getSongs() != null && !section.getSongs().isEmpty()) {

                        recyclerView.setLayoutManager(new LinearLayoutManager(
                                MainActivity.this,
                                LinearLayoutManager.HORIZONTAL,
                                false
                        ));

                        // 🔁 Convert List<Integer> to List<String>
                        List<String> stringIds = section.getSongs().stream()
                                .map(String::valueOf)
                                .collect(Collectors.toList());

                        // 🔍 Fetch the songs using whereIn on documentId
                        FirebaseFirestore.getInstance().collection("songs")
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

                                    // 🎯 Set adapter with fetched songs
                                    SectionSongListAdapter adapter = new SectionSongListAdapter(
                                            (ArrayList<SongModel>) songsList,
                                            MainActivity.this
                                    );
                                    recyclerView.setAdapter(adapter);
                                    // ✅ Show layout and set title AFTER data is ready
                                    mainLayout.setVisibility(View.VISIBLE);
                                    titleView.setText(section.getName());

                                });


                        // 🎬 Handle click to open full section
                        mainLayout.setOnClickListener(v -> {
                            Intent intent = new Intent(MainActivity.this, SongsListActivity.class);
                            intent.putExtra("category_name", section.getName());
                            intent.putExtra("category_coverURL", section.getCoverUrl());
                            ArrayList<Integer> songIds = new ArrayList<>(section.getSongs());
                            intent.putIntegerArrayListExtra("category_songs", songIds);
                            startActivity(intent);
                        });
                    }
                });
    }



}
