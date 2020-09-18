package com.dev.login_example;

public class USER {
    public USER() {
    }
    private String name,number,gender,imageUrl,dateOfBirth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public USER(String name, String number, String gender, String imageUrl, String dateOfBirth) {
        this.name = name;
        this.number = number;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.dateOfBirth = dateOfBirth;
    }
}
