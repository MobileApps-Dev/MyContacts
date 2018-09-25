package com.mycontacts.controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mycontacts.R;
import com.mycontacts.model.Contact_Model;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyHolder> {
    Context context;
    ArrayList<Contact_Model> contactArrayList;

    public ContactListAdapter(Context context, ArrayList<Contact_Model> contactArrayList) {
        this.context = context;
        this.contactArrayList = contactArrayList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        Contact_Model contactModel = contactArrayList.get(position);
        try {
            String contactName = contactModel.getContactName();
            String contactNumberMobile = contactModel.getContactNumberMobile();
            String contactNumberHome = contactModel.getContactNumberHome();
            String contactNumberWork = contactModel.getContactNumberWork();
            String imagePath = contactModel.getContactPhoto();

            if (!contactName.equals("") && contactName != null) {
                holder.name.setText(contactName);
            } else {
                holder.name.setText("No Name");
            }


            try {
                if (!contactNumberMobile.equals("") && contactNumberMobile != null) {
                    holder.phoneNumber.setText(contactNumberMobile);
                } else {
//                    if (!contactNumberHome.equals("") && contactNumberHome != null) {
//                        holder.phoneNumber.setText(contactNumberHome);
//                    } else {
//                        holder.phoneNumber.setText(contactNumberWork);
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (imagePath != null) {
                Glide.with(context).load(imagePath).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.img_photo) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.img_photo.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } else {
                holder.img_photo.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar));
            }

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView img_photo, img_faviourt;
        TextView name, phoneNumber;

        public MyHolder(View itemView) {
            super(itemView);

            img_photo = (ImageView) itemView.findViewById(R.id.img_photo);
            img_faviourt = (ImageView) itemView.findViewById(R.id.img_faviourt);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            phoneNumber = (TextView) itemView.findViewById(R.id.txt_number);
            phoneNumber = (TextView) itemView.findViewById(R.id.txt_number);
        }

    }
}
