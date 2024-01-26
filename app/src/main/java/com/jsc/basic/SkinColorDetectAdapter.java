package com.jsc.basic;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jsc.basic.databinding.ListItemSkinColorDetectBinding;
import com.jsc.basic.entities.SkinColorItem;

import java.util.ArrayList;
import java.util.List;

public class SkinColorDetectAdapter extends RecyclerView.Adapter<SkinColorDetectAdapter.SCDViewHolder> {

    final List<SkinColorItem> items = new ArrayList<>();

    public void setItems(List<SkinColorItem> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SCDViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemSkinColorDetectBinding binding = ListItemSkinColorDetectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SCDViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SCDViewHolder holder, int position) {
        holder.mBinding.ivResult.setImageBitmap(items.get(position).bitmap);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class SCDViewHolder extends RecyclerView.ViewHolder {

        ListItemSkinColorDetectBinding mBinding;

        public SCDViewHolder(@NonNull ListItemSkinColorDetectBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
