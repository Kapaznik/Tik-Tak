package com.android.TikTak.Models;

public class UserModel {
    String firstName;
    String lastName;
    String phone;
    String mail;
    String registerDate;
    int itemSold;
    int itemBought;

    public UserModel(String firstName, String lastName, String phone, String mail, String registerDate, int itemSold, int itemBought) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.mail = mail;
        this.registerDate = registerDate;
        this.itemSold = itemSold;
        this.itemBought = itemBought;
    }

    public UserModel(String firstName, String lastName, String phone, String mail, String registerDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.mail = mail;
        this.registerDate = registerDate;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public int getItemSold() {
        return itemSold;
    }

    public void setItemSold(int itemSold) {
        this.itemSold = itemSold;
    }

    public int getItemBought() {
        return itemBought;
    }

    public void setItemBought(int itemBought) {
        this.itemBought = itemBought;
    }

}