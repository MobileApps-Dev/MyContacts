package com.mycontacts.controller;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycontacts.R;
import com.mycontacts.model.M_Contact1;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyHolder> {
    Context context;
    ArrayList<M_Contact1> mContacts;

    public ContactAdapter(Context context, ArrayList<M_Contact1> mContacts) {
        this.context = context;
        this.mContacts = mContacts;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        M_Contact1 contact = mContacts.get(position);
        try {
            String mob = contact.getContact_Number();
            String name = contact.getContact_Name();

//           int abc= contact.getPhoneList().size();
//            String mob =   contact.getPhoneList().get(0).getDataValue();


            //System.out.println("mob  " + mob);
            //   if (mob != null) {
            holder.name.setText(name);
             holder.phoneNumber.setText(mob);
//                String imagePath = contact.getPhoto();
//                if (imagePath != null) {
//                    Glide.with(context).load(imagePath).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.img_photo) {
//                        @Override
//                        protected void setResource(Bitmap resource) {
//                            RoundedBitmapDrawable circularBitmapDrawable =
//                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
//                            circularBitmapDrawable.setCircular(true);
//                            holder.img_photo.setImageDrawable(circularBitmapDrawable);
//                        }
//                    });
//                } else {
//                    holder.img_photo.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar));
//                }
            // }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

//        holder.img_faviourt.setImageDrawable(context.getResources().getDrawable(R.drawable.star_un));

//        holder.img_faviourt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    M_Contact contact = mContacts.get(position);
//                    String contact_Id = contact.getContact_ID();
//                    String contact_name = contact.getContact_Name();
//                    String contactNumber = contact.getContact_Number();
//                    String imagePath = contact.getContact_Image_uri();
//
//                    // update data to of localstorage
//                    LocalStorageHandler localStorageHandler = new LocalStorageHandler(context);
//                    //  change status 0 to 1
//                    localStorageHandler.updateData(contact_Id, contact_name, contactNumber, imagePath, "1");
//                    holder.img_faviourt.setImageDrawable(context.getResources().getDrawable(R.drawable.star));
//                } catch (Resources.NotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
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
