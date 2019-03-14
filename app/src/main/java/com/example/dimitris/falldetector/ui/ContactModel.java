package com.example.dimitris.falldetector.ui;

import java.io.Serializable;

public class ContactModel implements Serializable {

    private String name;
    private String phone;
    private int pos;

    public ContactModel() {
    }

    public ContactModel(String name, String phone, int pos) {
        this.name = name;
        this.phone = phone;
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", pos=" + pos +
                '}';
    }
}
