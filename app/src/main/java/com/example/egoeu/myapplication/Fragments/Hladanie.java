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
public class Hladanie extends Fragment {

    private List<CardPodnik> hladanie_list;
    private RecyclerView podniky_view_list;

    private PodnikRecyclerAdapter podnikRecyclerAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public Hladanie() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hladanie, container, false);

        hladanie_list = new ArrayList<>();
        podniky_view_list = view.findViewById(R.id.hladanie_list);

        firebaseAuth = FirebaseAuth.getInstance();

        podnikRecyclerAdapter = new PodnikRecyclerAdapter(hladanie_list);
        podniky_view_list.setLayoutManager(new LinearLayoutManager(container.getContext()));
        podniky_view_list.setAdapter(podnikRecyclerAdapter);
        podniky_view_list.setHasFixedSize(true);

        if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            podniky_view_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {
                    }
                }
            });

            //extrass ktore som si sem posielal z main triedy vlastne slova ktore som hladal
            Bundle bundle = this.getArguments();
            final String s = bundle.getString("hladat");
            //debug
            Log.d("preslo to do fragmentu", s);

            //vytiahneme si z databazi podniky zoradime podla hodnotenia a v aplikacenj logike si ich if statementom prejdeme
            //ci neobsahuju vyhladavane slovo prehladava sa podla nazvu adresy a typu podniku
            Query firstQuery = firebaseFirestore.collection("podnikTester").orderBy("hodnotenie", Query.Direction.DESCENDING).limit(100);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (!documentSnapshots.isEmpty()) {

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String cardPodnikId = doc.getDocument().getId();
                                CardPodnik cardPodnik = doc.getDocument().toObject(CardPodnik.class).withId(cardPodnikId);

                                if(cardPodnik.getNazov().toLowerCase().contains(s)) {

                                    hladanie_list.add(cardPodnik);
                                    podnikRecyclerAdapter.notifyDataSetChanged();

                                } else if (cardPodnik.getAdresa().toLowerCase().contains(s)){

                                    hladanie_list.add(cardPodnik);
                                    podnikRecyclerAdapter.notifyDataSetChanged();

                                } else if (cardPodnik.getTyp().toLowerCase().contains(s)){

                                    hladanie_list.add(cardPodnik);
                                    podnikRecyclerAdapter.notifyDataSetChanged();

                                }
                            }
                        }
                    }
                }
            });
        }
        // Inflate the layout for this fragment
        return view;
    }
}
