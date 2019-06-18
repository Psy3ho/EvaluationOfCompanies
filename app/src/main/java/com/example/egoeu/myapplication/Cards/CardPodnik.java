package com.example.egoeu.myapplication.Cards;

/**
 * Created by egoeu on 6. 4. 2018.
 */

public class CardPodnik extends com.example.egoeu.myapplication.Cards.CardPodnikId {



    public String nazov, telefon, adresa, popis, image, typ;
    public Integer countOblubene,hodnotenie;


    public  CardPodnik() {};


    public CardPodnik(String nazov, String telefon, String adresa, String popis, String image_url,String typ, Integer countOblubene,Integer hodnotenie) {
        this.nazov = nazov;
        this.telefon = telefon;
        this.adresa = adresa;
        this.popis = popis;
        this.image = image_url;
        this.typ =typ;
        this.countOblubene=countOblubene;
        this.hodnotenie=hodnotenie;
    }

    public Integer getHodnotenie() { return hodnotenie; }

    public void setHodnotenie(Integer hodnotenie) {  this.hodnotenie = hodnotenie; }

    public Integer getCountOblubene() {
        return countOblubene;
    }

    public void setCountOblubene(Integer countOblubene) {
        this.countOblubene = countOblubene;
    }

    public String getTyp() { return typ; }

    public String getNazov() {
        return nazov;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getPopis() {
        return popis;
    }

    public String getImage_url() {
        return image;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public void setPopis(String popis) { this.popis = popis;    }

    public void setImage_url(String image_url) {
        this.image = image_url;
    }
}
