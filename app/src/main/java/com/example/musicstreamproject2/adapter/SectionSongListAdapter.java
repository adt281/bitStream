package com.example.musicstreamproject2.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicstreamproject2.R;

public class SectionSongListAdapter  extends RecyclerView.Adapter<SectionSongListAdapter.MyViewHolder>{



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //These ids are for the cards and not the title of the recyclerview, you'll set those from the outside
            //inside recycler view you can only set the cards !!! remember that !!

            textView=itemView.findViewById(R.id.song_title_text_view);
            imageView=itemView.findViewById(R.id.song_cover_image_view);


        }
    }

}
