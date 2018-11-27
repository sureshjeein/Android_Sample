package com.jpmc.samplealbum.samplealbumlist.RecyclerViewer;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import com.jpmc.samplealbum.samplealbumlist.R;
import com.jpmc.samplealbum.samplealbumlist.SampleAlbum;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<SampleAlbum> dataSet;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtUserId;
        public TextView txtAlbumId;
        public TextView txtAlbumTitle;
        public View layout;

        public MyViewHolder(View v) {
            super(v);
            layout = v;
            txtUserId = (TextView) v.findViewById(R.id.userId);
            txtAlbumId = (TextView) v.findViewById(R.id.albumId);
            txtAlbumTitle = (TextView) v.findViewById(R.id.albumTitle);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public Adapter(List<SampleAlbum> myDataset) {
        this.dataSet = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(position %2 == 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.txtAlbumTitle.setText("Title: " + dataSet.get(position).getTitle());
            holder.txtAlbumId.setText("Id: " + dataSet.get(position).getId());
            holder.txtUserId.setText("User Id: " + dataSet.get(position).getUserId());
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
            holder.txtAlbumTitle.setText("Title: " + dataSet.get(position).getTitle());
            holder.txtAlbumId.setText("Id: " + dataSet.get(position).getId());
            holder.txtUserId.setText("User Id: " + dataSet.get(position).getUserId());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}