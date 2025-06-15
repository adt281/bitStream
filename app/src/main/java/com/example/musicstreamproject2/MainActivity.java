package com.example.musicstreamproject2;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicstreamproject2.adapter.CategoryAdapter;
import com.example.musicstreamproject2.models.CategoryModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;

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



        //TODO: Recycler view Horizontal setup
        recyclerView = findViewById(R.id.categories_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        getCategories();

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




}