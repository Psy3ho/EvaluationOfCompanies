package com.example.egoeu.myapplication.Cards;

import android.support.annotation.NonNull;

/**
 * Created by egoeu on 7. 4. 2018.
 */


public class CardKomentarId {
    public String CardKomentarId;

    public <T extends com.example.egoeu.myapplication.Cards.CardKomentarId> T withId(@NonNull final String id) {
        this.CardKomentarId = id;
        return (T) this;
    }
}


