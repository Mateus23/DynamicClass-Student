package com.example.mateu.dynamicclass_student;

public class UserInfo {

    public String name;

    public String completeName;

    public String email;


    public UserInfo() {

    }


    public UserInfo(String name, String lastName, String email){
        this.name = name;
        this.completeName= name + " " + lastName;
        this.email = email;

    }

}