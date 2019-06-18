package com.example.egoeu.myapplication.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.egoeu.myapplication.R;
import com.example.egoeu.myapplication.Adapters.KomentarRecyclerAdapter;
import com.example.egoeu.myapplication.Cards.CardKomentar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Koment extends Fragment {

    private FirebaseAuth mAuth;

    private List<CardKomentar> komentarList;
    private RecyclerView komentare_view_list;
    private KomentarRecyclerAdapter komentarRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String typPodniku;
    private String idPodniku;

    public Koment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_koment, container, false);

        komentarList = new ArrayList<>();
        komentare_view_list = view.findViewById(R.id.comment_list);

        firebaseAuth = FirebaseAuth.getInstance();

        komentarRecyclerAdapter = new KomentarRecyclerAdapter(komentarList);
        komentare_view_list.setLayoutManager(new LinearLayoutManager(container.getContext()));
        komentare_view_list.setAdapter(komentarRecyclerAdapter);
        komentare_view_list.setHasFixedSize(true);

        if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            //scrollovanie komentarov
            komentare_view_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);

                }
            });

            //extras ktore si posleme z podniku ktory komentujeme
            Bundle bundle = this.getArguments();
            String typPodnikuK = bundle.getString("typk");
            String podnikIdK = bundle.getString("podnikIdk");
            //debug
            Log.d(typPodnikuK +podnikIdK,typPodnikuK +podnikIdK);

            //Vytiahneme komentare z databaze a zoradime ich podla casu ako boli pridane
            firebaseFirestore.collection("podnikTester").document(podnikIdK).collection("Komentare").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String cardKomentarId = doc.getDocument().getId();
                                CardKomentar cardKomentar = doc.getDocument().toObject(CardKomentar.class).withId(cardKomentarId);
                                komentarList.add(cardKomentar);
                                komentarRecyclerAdapter.notifyDataSetChanged();

                            }
                        }
                    }
                }
            });
        }
        return view;
    }
}
