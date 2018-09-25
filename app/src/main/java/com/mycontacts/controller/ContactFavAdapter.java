package com.mycontacts.controller;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mycontacts.R;
import com.mycontacts.model.M_ContactFAV;

import java.util.ArrayList;


public class ContactFavAdapter extends RecyclerView.Adapter<ContactFavAdapter.MyHolder> {
    Context context;
    ArrayList<M_ContactFAV> mContacts;

    public ContactFavAdapter(Context context, ArrayList<M_ContactFAV> mContacts) {
        this.context = context;
        this.mContacts = mContacts;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_fav_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        try {
            M_ContactFAV contact = mContacts.get(position);
            String imagePath = contact.getContact_Image_uri();
            String contact_status = contact.getContact_Status();

            holder.name.setText(contact.getContact_Name());

            if (imagePath != null) {
                Glide.with(context).load(imagePath).into(holder.img_photo);
            } else {
                holder.img_photo.setImageDrawable(context.getResources().getDrawable(R.drawable.th));
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView img_photo;
        TextView name;

        public MyHolder(View itemView) {
            super(itemView);

            img_photo = (ImageView) itemView.findViewById(R.id.img_photo);
            name = (TextView) itemView.findViewById(R.id.txt_name);
        }

    }
}
