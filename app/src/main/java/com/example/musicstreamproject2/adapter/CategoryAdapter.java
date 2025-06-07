package com.example.musicstreamproject2.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
        holder.textView.setText(categoryModel.getName());
        //TODO: setup glide and load images using URL
        // .thumbnail() deprecated use this [suggested by glide documentation to replecate .thumbnail()]
        RequestBuilder<Drawable> thumbnailRequest = Glide.with(holder.itemView.getContext())
                .load(categoryModel.getCoverURL())
                .sizeMultiplier(0.01f); // tiny thumbnail

        Glide.with(holder.itemView.getContext())
                .load(categoryModel.getCoverURL())
                .thumbnail(thumbnailRequest) // ✅ RequestBuilder with multiplier
                .sizeMultiplier(0.8f)        // ✅ final image size 80%
                .diskCacheStrategy(DiskCacheStrategy.ALL) // optional
                .into(holder.imageView);


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
