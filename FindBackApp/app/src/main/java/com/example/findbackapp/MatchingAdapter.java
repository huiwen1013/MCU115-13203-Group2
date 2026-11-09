package com.example.findbackapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MatchingAdapter extends RecyclerView.Adapter<MatchingAdapter.MatchingViewHolder> {

    public interface OnMatchActionListener {
        void onDetailClick(MatchingResult result);
        void onClaimClick(MatchingResult result);
    }

    private final Context context;
    private List<MatchingResult> matchResults;
    private OnMatchActionListener listener;

    public MatchingAdapter(Context context, List<MatchingResult> matchResults) {
        this.context = context;
        this.matchResults = matchResults != null ? matchResults : new ArrayList<>();
    }

    public void setOnMatchActionListener(OnMatchActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<MatchingResult> newResults) {
        this.matchResults = newResults != null ? newResults : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_matching_card, parent, false);
        return new MatchingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchingViewHolder holder, int position) {
        MatchingResult result = matchResults.get(position);
        Item matchedItem = result.getMatchedItem();

        holder.tvName.setText(matchedItem.getItemName());
        holder.tvCategoryColor.setText(matchedItem.getCategory() + " | " + matchedItem.getColor());
        holder.tvLocationDate.setText(matchedItem.getLocation() + " (" + matchedItem.getItemDate() + ")");

        int score = result.getScore();
        holder.tvScore.setText(score + "%");
        holder.tvLevelBadge.setText(result.getMatchLevel());

        int colorRes = R.color.match_low;
        if (score >= 80) {
            colorRes = R.color.match_high;
        } else if (score >= 60) {
            colorRes = R.color.match_medium;
        }

        int badgeColor = ContextCompat.getColor(context, colorRes);
        holder.tvScore.setTextColor(badgeColor);
        holder.tvLevelBadge.setBackgroundColor(badgeColor);

        if (matchedItem.getImageUri() != null && !matchedItem.getImageUri().isEmpty()) {
            try {
                Uri uri = Uri.parse(matchedItem.getImageUri());
                holder.imgPhoto.setImageURI(uri);
                if (holder.imgPhoto.getDrawable() == null) {
                    holder.imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } catch (Throwable e) {
                holder.imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) listener.onDetailClick(result);
        });

        holder.btnClaim.setOnClickListener(v -> {
            if (listener != null) listener.onClaimClick(result);
        });
    }

    @Override
    public int getItemCount() {
        return matchResults.size();
    }

    static class MatchingViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView tvName;
        TextView tvCategoryColor;
        TextView tvLocationDate;
        TextView tvScore;
        TextView tvLevelBadge;
        Button btnDetail;
        Button btnClaim;

        public MatchingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_match_item);
            tvName = itemView.findViewById(R.id.tv_match_item_name);
            tvCategoryColor = itemView.findViewById(R.id.tv_match_category_color);
            tvLocationDate = itemView.findViewById(R.id.tv_match_location_date);
            tvScore = itemView.findViewById(R.id.tv_match_score);
            tvLevelBadge = itemView.findViewById(R.id.tv_match_level_badge);
            btnDetail = itemView.findViewById(R.id.btn_match_detail);
            btnClaim = itemView.findViewById(R.id.btn_match_claim);
        }
    }
}
