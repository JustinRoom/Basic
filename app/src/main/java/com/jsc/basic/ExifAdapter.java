package com.jsc.basic;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.RecyclerView;

import com.jsc.basic.databinding.ListItemExifBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ExifAdapter extends RecyclerView.Adapter<ExifAdapter.EViewHolder> {

    final List<File> files = new ArrayList<>();

    public void addFiles(File[] files) {
        if (files != null) {
            this.files.addAll(Arrays.asList(files));
            notifyDataSetChanged();
        }
    }

    public void addFile(File f) {
        this.files.add(0, f);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public EViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemExifBinding binding = ListItemExifBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EViewHolder holder, int position) {
        holder.mBinding.tvPicName.setText(files.get(position).getName());
        try {
            ExifInterface face = new ExifInterface(files.get(position));
            holder.mBinding.tvPicUniqueId.setText(String.format(Locale.US, "UniqueId:%s\u3000BodySerialNumber:%s",
                    face.getAttribute(ExifInterface.TAG_IMAGE_UNIQUE_ID),
                    face.getAttribute(ExifInterface.TAG_BODY_SERIAL_NUMBER)));
        } catch (IOException e) {
            holder.mBinding.tvPicUniqueId.setText("");
        }
        holder.mBinding.getRoot().setBackgroundColor(position % 2 == 1 ? 0xFFF2F2F2 : Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class EViewHolder extends RecyclerView.ViewHolder {

        ListItemExifBinding mBinding;

        public EViewHolder(@NonNull ListItemExifBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
