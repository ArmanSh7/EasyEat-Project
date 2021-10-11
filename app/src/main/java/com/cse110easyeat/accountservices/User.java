package com.cse110easyeat.accountservices;

public class User {
    String id;
    String email;
    String fullName;
//    String[] favoriteRestaurants;

    public User(String email, String fullName) {
        // CHECK REPLACE ALL
        String modifiedEmailStr = email.replaceAll("\\.","_");
        this.id = modifiedEmailStr;
        this.fullName = fullName;
        this.email = email;
    }

    public User() {}

    public String getId() {
        return id;
    }
    public String getEmail() {return email;}
    public String getFullName() {return fullName;}
}
