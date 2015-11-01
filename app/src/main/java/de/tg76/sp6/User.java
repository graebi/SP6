package de.tg76.sp6;

/**
 * User class - Creates user and holds attributes of user
 */
public class User {

    //Variable to hold value of user
    final String name;
    final String username;
    final String password;
    final String email;

    //Constructor when user register
    public User (String name, String email,String username, String password){
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    //Constructor when user logs on
    public User (String username, String password){
        this.username = username;
        this.password = password;
        this.email = "";
        this.name = "";
    }
}
