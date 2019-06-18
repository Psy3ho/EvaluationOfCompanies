package com.example.egoeu.myapplication.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.egoeu.myapplication.R;
import com.example.egoeu.myapplication.Adapters.PodnikRecyclerAdapter;
import com.example.egoeu.myapplication.Cards.CardPodnik;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
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
public class OblubeneFragment extends Fragment {

    private List<CardPodnik> oblubene_list;
    private RecyclerView podniky_view_list;
    private PodnikRecyclerAdapter podnikRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public OblubeneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_oblubene, container, false);

        oblubene_list = new ArrayList<>();
        podniky_view_list = view.findViewById(R.id.oblubene_list);

        firebaseAuth = FirebaseAuth.getInstance();

        podnikRecyclerAdapter = new PodnikRecyclerAdapter(oblubene_list);
        podniky_view_list.setLayoutManager(new LinearLayoutManager(container.getContext()));
        podniky_view_list.setAdapter(podnikRecyclerAdapter);
        podniky_view_list.setHasFixedSize(true);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            //scrollovanie oblbuenych
            podniky_view_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                }
            });

            /*
            *vytiahneme z databazky podniky zoradime ich podla najlepsieho hodnotenia
            * prechadzame objekt za objektom a pri kazdom
            * znova volame do databazky kde uz ideme podla jednotliveho podniku na jeho zoznam ktory
            * zaznam ktory ho maju oblubeny a pokial podla id uzivatela existuje taky objekt v nasom pripade timestamp
            * podnik je pridany do listu na zobrazenie v tomto fragmente
             */
            Query firstQuery = firebaseFirestore.collection("podnikTester").orderBy("hodnotenie", Query.Direction.DESCENDING).limit(100);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        for (final DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String cardPodnikId = doc.getDocument().getId();
                                final String currentUserId = firebaseAuth.getCurrentUser().getUid();

                                //druhe volanie do databazky na ci ma prihlaseny uzivatel podnik v cikle v oblbuenych
                                firebaseFirestore.collection("podnikTester")
                                        .document(cardPodnikId)
                                        .collection("Oblubene")
                                        .document(currentUserId)
                                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                                                if (documentSnapshot.exists()) {

                                                    String cardPodnikId = doc.getDocument().getId();
                                                    CardPodnik cardPodnik = doc.getDocument().toObject(CardPodnik.class).withId(cardPodnikId);
                                                    oblubene_list.add(cardPodnik);
                                                    podnikRecyclerAdapter.notifyDataSetChanged();

                                                }
                                            }

                                        });
                            }
                        }
                       podnikRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        return view;
    }
}
