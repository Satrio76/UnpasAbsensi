package com.example.unpasabsensi.absensi.model;

public class UserMhs {
    private int id;
    private String nim;
    private String password;
    private String nama;
    private String status;
    private String upload_date;

    public String getMac_user() {
        return mac_user;
    }

    public void setMac_user(String mac_user) {
        this.mac_user = mac_user;
    }

    private String mac_user;
    public UserMhs(){

    }

    public UserMhs(UserMhs user){
        try {
            this.id = user.getId();
            this.nim = user.getNim();
            this.password = user.getPassword();
            this.status = user.getStatus();
            this.upload_date = user.getUpload_date();
            this.nama = user.getNama();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public UserMhs(int id, String nim, String password, String status, String upload_date, String nama) {
        this.id = id;
        this.nim = nim;
        this.password = password;
        this.status = status;
        this.upload_date = upload_date;
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }
}
