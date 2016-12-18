package com.egco428.graduation;

/**
 * Created by Lostl2ose on 12/17/2016.
 */

//// สำหรับเก็บข้อมูล
public class Graduate {
    private String username;
    private String password;
    private String confirmpass;
    private String firstname;
    private String lastname;
    private String mobile;
    private String randomnum;

    public Graduate(String username,String password,String confirmpass,String firstname,String lastname,String mobile,String randomnum){
        this.username = username;
        this.password = password;
        this.confirmpass = confirmpass;
        this.firstname = firstname;
        this.lastname = lastname;
        this.mobile = mobile;
        this.randomnum = randomnum;
    }

    public  Graduate(){}

    public String getUsername(){return username;}
    public String getPassword(){return password;}
    public String getConfirmpass(){return confirmpass;}
    public String getFirstname(){return firstname;}
    public String getLastname(){return lastname;}
    public String getMobile(){return mobile;}
    public String getRandomnum(){return randomnum;}
}
