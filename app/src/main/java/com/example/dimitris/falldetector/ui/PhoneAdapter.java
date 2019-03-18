package com.example.dimitris.falldetector.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dimitris.falldetector.R;

import java.util.List;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder> {
    private List<ContactModel> listContact;
    private Context context;
    private onItemClickClick onItemClick;

    public PhoneAdapter(List<ContactModel> listContact, Context context, onItemClickClick onItemClick) {
        this.listContact = listContact;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @Override
    public PhoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhoneViewHolder(LayoutInflater.from(context).inflate(R.layout.item_phone, parent, false));
    }

    @Override
    public void onBindViewHolder(PhoneViewHolder holder, final int position) {
        final ContactModel contact = listContact.get(position);
        if (contact.getPhone() == "") {
            holder.ivAdd.setVisibility(View.VISIBLE);
            holder.ivRemove.setVisibility(View.GONE);
            holder.ivAdd.setImageResource(R.drawable.ic_add_circle_blue);
            holder.txtContact.setText(contact.getName());
        } else {
            holder.ivAdd.setVisibility(View.GONE);
            holder.ivRemove.setVisibility(View.VISIBLE);
            holder.txtContact.setText(contact.getPhone());
        }

        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.addItem(contact);
            }
        });

        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.removeItem(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listContact.size();
    }

    public class PhoneViewHolder extends RecyclerView.ViewHolder {
        TextView txtContact;
        ImageView ivAdd;
        ImageView ivRemove;

        public PhoneViewHolder(View itemView) {
            super(itemView);
            txtContact = (TextView) itemView.findViewById(R.id.contact);
            ivAdd = (ImageView) itemView.findViewById(R.id.ivAdd);
            ivRemove = (ImageView) itemView.findViewById(R.id.ivRemove);
        }
    }

    interface onItemClickClick {
        void addItem(ContactModel contact);

        void removeItem(ContactModel contact);
    }
}
