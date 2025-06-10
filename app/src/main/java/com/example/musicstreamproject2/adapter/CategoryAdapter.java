package com.example.musicstreamproject2.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicstreamproject2.R;
import com.example.musicstreamproject2.SongsListActivity;
import com.example.musicstreamproject2.models.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    ArrayList<CategoryModel> arrayList;
    Context context;
    public CategoryAdapter(ArrayList<CategoryModel> arrayList, Context context)
    {
        this.arrayList=arrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater. from(parent.getContext()).inflate(R.layout.category_item_recycler_row, parent,
                 false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoryModel categoryModel=arrayList.get(position);

        //set the parameters:
        holder.textView.setText(categoryModel.getName());


        // setup glide and load images using URL
        // .thumbnail() deprecated use this [suggested by glide documentation to replecate .thumbnail()]
        RequestBuilder<Drawable> thumbnailRequest = Glide.with(holder.itemView.getContext())
                .load(categoryModel.getCoverURL())
                .sizeMultiplier(0.01f); // tiny thumbnail

        Glide.with(holder.itemView.getContext())
                .load(categoryModel.getCoverURL())
                .thumbnail(thumbnailRequest) // ✅ RequestBuilder with multiplier
                .sizeMultiplier(0.6f)        // ✅ final image size 60%
                .diskCacheStrategy(DiskCacheStrategy.ALL) // optional
                .into(holder.imageView);

        //Log.d for the list of ID's:
        Log.d("IDS",""+categoryModel.getSongs());

        //we have got the ID's of songs associated with every Category, on click we just need to send these
        //Ids now to the SongsListActivity

        //click listener for card:
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Launching SongsListActivity and Sending all the data !!!!
                Intent intent = new Intent(context, SongsListActivity.class);
                intent.putExtra("category_name", categoryModel.getName());
                intent.putExtra("category_coverURL", categoryModel.getCoverURL());
                intent.putIntegerArrayListExtra("category_songs", new ArrayList<>(categoryModel.getSongs()));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.cover_image_viewID);
            textView=itemView.findViewById(R.id.name_text_viewID);
        }
    }

}
