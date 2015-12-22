package de.tg76.sp6;

/**
 * User class - Creates user and holds attributes of user
 * Thorsten Graebner D11123994
 */
public class User {

    //Variable to hold value of user
    final String name;
    final String username;
    final String password;
    final String email;
    final int customer_id;

    //Constructor when user register
    public User (int customer_id,String name, String email,String username, String password){

        this.customer_id = customer_id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    //Constructor when user logs on
    public User (int id,String username, String password){
        this.customer_id = id;
        this.username = username;
        this.password = password;
        this.email = "";
        this.name = "";
    }
}
