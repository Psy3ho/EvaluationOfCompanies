package com.example.egoeu.myapplication.Cards;

/**
 * Created by egoeu on 6. 4. 2018.
 */

import android.support.annotation.NonNull;

public class CardPodnikId {
    public String CardPodnikId;

    public <T extends com.example.egoeu.myapplication.Cards.CardPodnikId> T withId(@NonNull final String id) {
        this.CardPodnikId = id;
        return (T) this;
    }
}
