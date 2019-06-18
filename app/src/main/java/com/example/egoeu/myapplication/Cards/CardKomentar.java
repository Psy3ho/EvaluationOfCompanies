package com.example.egoeu.myapplication.Cards;

import java.util.Date;

/**
 * Created by egoeu on 7. 4. 2018.
 */

public class CardKomentar extends CardKomentarId{

    String email, komentar, user_id, podnik_id;
    public Date timestamp;


    public CardKomentar(){};

    public CardKomentar(String email, String komentar, String user_id, String podnik_id, Date timestamp) {
        this.email = email;
        this.komentar = komentar;
        this.user_id = user_id;
        this.podnik_id = podnik_id;
        this.timestamp = timestamp;
    }


    public String getEmail() {
        return email;
    }

    public String getKomentar() {
        return komentar;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPodnik_id() {
        return podnik_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setPodnik_id(String podnik_id) {
        this.podnik_id = podnik_id;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
