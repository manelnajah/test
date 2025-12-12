package com.example.securecontacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.VH> {

    public interface OnItemInteraction {
        void onDelete(int position);
        void onCall(int position);
        void onSms(int position);     // 👉 ajout
    }

    private List<Contact> items;
    private OnItemInteraction callback;

    public ContactsAdapter(List<Contact> items, OnItemInteraction callback) {
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Contact c = items.get(position);

        holder.tvName.setText(c.getName());
        holder.tvPhone.setText(c.getPhone());

        // APPELER
        holder.btnCall.setOnClickListener(v -> {
            if (callback != null) callback.onCall(position);
        });

        // SUPPRIMER
        holder.btnDelete.setOnClickListener(v -> {
            if (callback != null) callback.onDelete(position);
        });

        // SMS
        holder.btnSms.setOnClickListener(v -> {
            if (callback != null) callback.onSms(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvName, tvPhone;
        ImageButton btnDelete, btnCall, btnSms;

        public VH(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnSms = itemView.findViewById(R.id.btnSms);
        }
    }
}
