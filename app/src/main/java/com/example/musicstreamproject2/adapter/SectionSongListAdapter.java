//TODO: same working as SongListAdapter.

/* TODO: creating this adapter and not re-using SongListAdapter for future operations where hybrid items
    [songs and albums] might need to be shown
*/
package com.example.musicstreamproject2.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.musicstreamproject2.models.SongModel;
import com.example.musicstreamproject2.player.MyExoPlayer;
import com.example.musicstreamproject2.player.PlayerActivity;

import java.util.ArrayList;

public class SectionSongListAdapter extends RecyclerView.Adapter<SectionSongListAdapter.MyViewHolder> {

    ArrayList<SongModel> arrayList;

    Context context;

    //user defined constructor:
    public SectionSongListAdapter(ArrayList<SongModel> arrayList, Context context)
    {
        this.arrayList=arrayList;
        this.context=context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater. from(parent.getContext()).inflate(R.layout.section_song_list_recycler_row, parent,
                false);

        return new SectionSongListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SongModel songModel = arrayList.get(position);
        holder.textViewTitle.setText(songModel.getTitle());
        holder.textViewSubtitle.setText(songModel.getSubtitle());

        //TODO: load the image using glide.
        // setup glide and load images using URL
        // .thumbnail() deprecated use this [suggested by glide documentation to replecate .thumbnail()]
        RequestBuilder<Drawable> thumbnailRequest = Glide.with(holder.itemView.getContext())
                .load(songModel.getCoverUrl())
                .sizeMultiplier(0.01f); // tiny thumbnail

        Glide.with(holder.itemView.getContext())
                .load(songModel.getCoverUrl())
                .thumbnail(thumbnailRequest) // ✅ RequestBuilder with multiplier
                .sizeMultiplier(0.5f)        // ✅ final image size 10%
                .diskCacheStrategy(DiskCacheStrategy.ALL) // optional
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start playing the song
                MyExoPlayer.startPlaying(holder.itemView.getContext(), songModel);

                // Show mini player if context is MainActivity
                if (context instanceof com.example.musicstreamproject2.MainActivity) {
                    ((com.example.musicstreamproject2.MainActivity) context).showMiniPlayer(songModel);
                }

                // Start PlayerActivity
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, PlayerActivity.class);
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
        TextView textViewTitle;
        TextView textViewSubtitle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //These ids are for the cards and not the title of the recyclerview, you'll set those from the outside
            //inside recycler view you can only set the cards !!! remember that !!

            textViewTitle=itemView.findViewById(R.id.song_title_text_view);
            textViewSubtitle=itemView.findViewById(R.id.song_subtitle_text_view);
            imageView=itemView.findViewById(R.id.song_cover_image_view);
        }
    }

}
