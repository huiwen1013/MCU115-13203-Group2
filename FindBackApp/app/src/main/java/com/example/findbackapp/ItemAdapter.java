package com.example.findbackapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final Context context;
    private List<Item> itemList;
    private OnItemClickListener listener;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList != null ? itemList : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Item> newItems) {
        this.itemList = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.tvItemName.setText(item.getItemName());
        holder.tvItemCategoryColor.setText(context.getString(R.string.item_category) + "：" + item.getCategory()
                + " | " + context.getString(R.string.item_color) + "：" + item.getColor());
        holder.tvItemLocation.setText("地點：" + item.getLocation());
        holder.tvItemDate.setText("日期：" + item.getItemDate());

        if (Item.TYPE_LOST.equalsIgnoreCase(item.getItemType())) {
            holder.tvItemTypeBadge.setText("遺失");
            holder.tvItemTypeBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.lost_color));
        } else {
            holder.tvItemTypeBadge.setText("拾獲");
            holder.tvItemTypeBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.found_color));
        }

        if (Item.STATUS_RESOLVED.equalsIgnoreCase(item.getStatus())) {
            holder.tvItemStatus.setText("已找回");
            holder.tvItemStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        } else {
            holder.tvItemStatus.setText("刊登中");
            holder.tvItemStatus.setTextColor(ContextCompat.getColor(context, R.color.primary));
        }

        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            try {
                Uri uri = Uri.parse(item.getImageUri());
                holder.imgItem.setImageURI(uri);
                if (holder.imgItem.getDrawable() == null) {
                    holder.imgItem.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } catch (Throwable e) {
                holder.imgItem.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.imgItem.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView tvItemTypeBadge;
        TextView tvItemName;
        TextView tvItemCategoryColor;
        TextView tvItemLocation;
        TextView tvItemDate;
        TextView tvItemStatus;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);
            tvItemTypeBadge = itemView.findViewById(R.id.tv_item_type_badge);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemCategoryColor = itemView.findViewById(R.id.tv_item_category_color);
            tvItemLocation = itemView.findViewById(R.id.tv_item_location);
            tvItemDate = itemView.findViewById(R.id.tv_item_date);
            tvItemStatus = itemView.findViewById(R.id.tv_item_status);
        }
    }
}
