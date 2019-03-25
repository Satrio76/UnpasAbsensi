package com.example.unpasabsensi.absensi.model;

public class User {
    private int iduser;
    private String username="";
    private String password="";
    private String status="";
    private String kodedosen="";
    private String gelarnama="";

    public User(){}

    public User(int iddosen, String username, String password, String status, String kodedosen, String gelarnama)
    {
        this.iduser=iddosen;
        this.username=username;
        this.password=password;
        this.status=status;
        this.kodedosen=kodedosen;
        this.gelarnama=gelarnama;
    }

    public User(User user){
        try {
            this.iduser = user.getIduser();
            this.username = user.getUsername();
            this.password = user.getPassword();
            this.status = user.getStatus();
            this.kodedosen = user.getKodedosen();
            this.gelarnama = user.getGelarnama();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setIduser(int iduser){this.iduser = iduser;}
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setStatus(String status){this.status = status;}
    public void setKodedosen(String kodedosen){this.kodedosen = kodedosen;}
    public void setGelarnama(String gelarnama){this.gelarnama = gelarnama;}

    public int getIduser() {return iduser;}
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getStatus(){return status;}
    public String getKodedosen(){return kodedosen;}
    public String getGelarnama() {return gelarnama;}
}
