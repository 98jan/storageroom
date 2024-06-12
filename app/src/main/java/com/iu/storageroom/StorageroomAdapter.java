package com.iu.storageroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.iu.storageroom.model.Storageroom;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class StorageroomAdapter extends RecyclerView.Adapter<StorageroomAdapter.ViewHolder> {
    private List<Storageroom> storageroomList;
    private Context context;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;
    private int[] iconResIds;

    // Interface for edit click listener
    public interface OnEditClickListener {
        void onEditClick(Storageroom storageroom);
    }

    // Interface for delete click listener
    public interface OnDeleteClickListener {
        void onDeleteClick(Storageroom storageroom);
    }

    public StorageroomAdapter(Context context, List<Storageroom> storageroomList,
                              OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.storageroomList = storageroomList;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_storageroom_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Storageroom storageroom = storageroomList.get(position);
        holder.storageroomName.setText(storageroom.getName());

        //int iconResId = Integer.parseInt(storageroom.getSelectedIcon());
        int iconResId = storageroom.getSelectedIconInt();
        holder.storageroomImage.setImageResource(iconResId);

        // Edit button click listener
        holder.editButton.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(storageroom);
            }
        });

        // Delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(storageroom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storageroomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView storageroomName;
        public ImageView storageroomImage;
        public ImageView editButton;
        public ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            storageroomName = itemView.findViewById(R.id.storageroomName);
            storageroomImage = itemView.findViewById(R.id.storageroomImage);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
