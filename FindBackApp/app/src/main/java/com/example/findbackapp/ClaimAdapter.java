package com.example.findbackapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.ClaimViewHolder> {

    public interface OnClaimActionListener {
        void onAccept(Claim claim);
        void onReject(Claim claim);
        void onComplete(Claim claim);
    }

    private final Context context;
    private List<Claim> claimList;
    private final boolean isReceivedClaimsTab;
    private OnClaimActionListener listener;

    public ClaimAdapter(Context context, List<Claim> claimList, boolean isReceivedClaimsTab) {
        this.context = context;
        this.claimList = claimList != null ? claimList : new ArrayList<>();
        this.isReceivedClaimsTab = isReceivedClaimsTab;
    }

    public void setOnClaimActionListener(OnClaimActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Claim> newClaims) {
        this.claimList = newClaims != null ? newClaims : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClaimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_claim_card, parent, false);
        return new ClaimViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaimViewHolder holder, int position) {
        Claim claim = claimList.get(position);

        holder.tvItemName.setText(claim.getItemName() != null ? claim.getItemName() : "物品編號 #" + claim.getItemId());

        if (isReceivedClaimsTab) {
            holder.tvUserInfo.setText("申請人：" + (claim.getClaimantName() != null ? claim.getClaimantName() : "使用者 #" + claim.getClaimantId()));
        } else {
            holder.tvUserInfo.setText("刊登者：" + (claim.getOwnerName() != null ? claim.getOwnerName() : "使用者 #" + claim.getOwnerId()));
        }

        holder.tvDate.setText("申請時間：" + claim.getCreatedAt());
        holder.tvMessage.setText("認領說明：" + (claim.getMessage() != null ? claim.getMessage() : "無"));

        // Status badge setup
        String status = claim.getStatus();
        if (Claim.STATUS_PENDING.equalsIgnoreCase(status)) {
            holder.tvStatus.setText("等待確認");
            holder.tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_pending));
        } else if (Claim.STATUS_ACCEPTED.equalsIgnoreCase(status)) {
            holder.tvStatus.setText("已接受");
            holder.tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_accepted));
        } else if (Claim.STATUS_REJECTED.equalsIgnoreCase(status)) {
            holder.tvStatus.setText("已拒絕");
            holder.tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_rejected));
        } else if (Claim.STATUS_COMPLETED.equalsIgnoreCase(status)) {
            holder.tvStatus.setText("已完成找回");
            holder.tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_completed));
        }

        // Configure action buttons visibility and actions based on tab & status
        if (isReceivedClaimsTab && Claim.STATUS_PENDING.equalsIgnoreCase(status)) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.btnComplete.setVisibility(View.GONE);
        } else if (Claim.STATUS_ACCEPTED.equalsIgnoreCase(status)) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.btnComplete.setVisibility(View.VISIBLE);
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(claim);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(claim);
        });

        holder.btnComplete.setOnClickListener(v -> {
            if (listener != null) listener.onComplete(claim);
        });

        if (claim.getItemImageUri() != null && !claim.getItemImageUri().isEmpty()) {
            try {
                Uri uri = Uri.parse(claim.getItemImageUri());
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
    }

    @Override
    public int getItemCount() {
        return claimList.size();
    }

    static class ClaimViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView tvItemName;
        TextView tvUserInfo;
        TextView tvDate;
        TextView tvStatus;
        TextView tvMessage;
        LinearLayout layoutActions;
        Button btnAccept;
        Button btnReject;
        Button btnComplete;

        public ClaimViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_claim_item);
            tvItemName = itemView.findViewById(R.id.tv_claim_item_name);
            tvUserInfo = itemView.findViewById(R.id.tv_claim_user_info);
            tvDate = itemView.findViewById(R.id.tv_claim_date);
            tvStatus = itemView.findViewById(R.id.tv_claim_status);
            tvMessage = itemView.findViewById(R.id.tv_claim_message);
            layoutActions = itemView.findViewById(R.id.layout_claim_actions);
            btnAccept = itemView.findViewById(R.id.btn_accept_claim);
            btnReject = itemView.findViewById(R.id.btn_reject_claim);
            btnComplete = itemView.findViewById(R.id.btn_complete_claim);
        }
    }
}
