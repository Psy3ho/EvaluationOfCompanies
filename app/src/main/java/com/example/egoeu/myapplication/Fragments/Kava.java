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
public class Kava extends Fragment {

    private List<CardPodnik> kava_list;
    private RecyclerView podniky_view_list;
    private PodnikRecyclerAdapter podnikRecyclerAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public Kava() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater,final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kava, container, false);

        kava_list = new ArrayList<>();
        podniky_view_list = view.findViewById(R.id.kava_list);

        firebaseAuth = FirebaseAuth.getInstance();

        podnikRecyclerAdapter = new PodnikRecyclerAdapter(kava_list);
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

                        loadMorePost();

                    }
                }
            });

            /*
            * Z databazi vythiahneme podniky a zoradime podla hodnotenia
            * limit je len na 10 podnikov aby ich nevytiahlo vsetky
            * Prejde cely zoznam podnikov a pri kazdom je spravena logika tak aby tu na strane klienta
            * vytiahol sa cely zoznam a triedime ho az tu po priradeni podniku
            * podla dalsich poziadaviek pokial if statement ak splna pridama ho do Listu a upozornime na zmenu dat
            * */
            Query firstQuery = firebaseFirestore.collection("podnikTester").orderBy("hodnotenie", Query.Direction.DESCENDING).limit(10);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            kava_list.clear();

                        }
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String cardPodnikId = doc.getDocument().getId();
                                CardPodnik cardPodnik = doc.getDocument().toObject(CardPodnik.class).withId(cardPodnikId);

                                if(cardPodnik.getTyp().contains("Kaviareň")) {

                                    if (isFirstPageFirstLoad) {

                                        kava_list.add(cardPodnik);

                                    } else {

                                        kava_list.add(0, cardPodnik);

                                    }
                                }
                                podnikRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                        isFirstPageFirstLoad = false;
                    }
                }
            });
        }
        // Inflate the layout for this fragment
        return view;
    }

    // metoda na zobrazenie dalsich podnikov od posledneho ulozeneho vola sa ked sa dostaneme na spodok zoznamu
    public void loadMorePost(){

        if(firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("podnikTester").orderBy("hodnotenie", Query.Direction.DESCENDING).startAfter(lastVisible).limit(10);
            nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String cardPodnikId = doc.getDocument().getId();
                                CardPodnik cardPodnik = doc.getDocument().toObject(CardPodnik.class).withId(cardPodnikId);

                                if(cardPodnik.getTyp().contains("Kaviareň")) {

                                    kava_list.add(cardPodnik);

                                }
                                podnikRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }
}
