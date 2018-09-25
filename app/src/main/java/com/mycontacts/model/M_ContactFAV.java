package com.mycontacts.model;

public class M_ContactFAV {
    private int ID;
    private String contact_ID;
    private String contact_Name;
    private String contact_Number;
    private String contact_Image_uri;
    private String contact_Status;


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getContact_ID() {
        return contact_ID;
    }

    public void setContact_ID(String contact_ID) {
        this.contact_ID = contact_ID;
    }

    public String getContact_Name() {
        return contact_Name;
    }

    public void setContact_Name(String contact_Name) {
        this.contact_Name = contact_Name;
    }

    public String getContact_Number() {
        return contact_Number;
    }

    public void setContact_Number(String contact_Number) {
        this.contact_Number = contact_Number;
    }

    public String getContact_Image_uri() {
        return contact_Image_uri;
    }

    public void setContact_Image_uri(String contact_Image_uri) {
        this.contact_Image_uri = contact_Image_uri;
    }

    public String getContact_Status() {
        return contact_Status;
    }

    public void setContact_Status(String contact_Status) {
        this.contact_Status = contact_Status;
    }
}
