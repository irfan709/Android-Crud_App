package com.example.usersapp;

import java.io.Serializable;

public class Users implements Serializable {
    static String email;
    static String name;
    static String password;
    static byte[] image;

    public Users() {
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Users.email = email;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Users.name = name;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Users.password = password;
    }

    public static byte[] getImage() {
        return image;
    }

    public static void setImage(byte[] image) {
        Users.image = image;
    }
}
