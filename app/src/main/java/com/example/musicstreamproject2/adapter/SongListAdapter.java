package com.example.musicstreamproject2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicstreamproject2.R;
import com.example.musicstreamproject2.models.SongModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder>{

    //TODO: Instead of songModel objects we just take in the song ID's and obtain the SongModel from the songs database!!
    ArrayList<SongModel> arrayList;

    Context context;

    //user defined constructor:
    public SongListAdapter(ArrayList<SongModel> arrayList, Context context)
    {
        this.arrayList=arrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater. from(parent.getContext()).inflate(R.layout.song_list_item_recycler_row, parent,
                false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SongModel songModel = arrayList.get(position);
        holder.textViewTitle.setText(songModel.getTitle());
        holder.textViewSubtitle.setText(songModel.getSubtitle());



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewSubtitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.song_cover_image_view);
            textViewTitle=itemView.findViewById(R.id.song_title_text_view);
            textViewSubtitle=itemView.findViewById(R.id.song_subtitle_text_view);
        }
    }
}
