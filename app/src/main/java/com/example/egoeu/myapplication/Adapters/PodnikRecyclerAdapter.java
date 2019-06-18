package com.example.egoeu.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.egoeu.myapplication.R;
import com.example.egoeu.myapplication.Cards.CardPodnik;
import com.example.egoeu.myapplication.UI.Podnik;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by egoeu on 6. 4. 2018.
 */

public class PodnikRecyclerAdapter extends RecyclerView.Adapter<PodnikRecyclerAdapter.ViewHolder>{

    public List<CardPodnik> podnik_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public PodnikRecyclerAdapter(List<CardPodnik> podnik_list){

        this.podnik_list = podnik_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sinlge_podnik_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String podnikId = podnik_list.get(position).CardPodnikId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        String nazov = podnik_list.get(position).getNazov();
        String popis = podnik_list.get(position).getPopis();
        String adresa = podnik_list.get(position).getAdresa();
        String telefon = podnik_list.get(position).getTelefon();
        String image = podnik_list.get(position).getImage_url();
        final String typ = podnik_list.get(position).getTyp();

        //setter na nastavenie textu podniku zo zoznamu
        holder.setDataPodniku(nazov, telefon, adresa, popis, image);

        //sucet poctu komentarov
        firebaseFirestore.collection("podnikTester").document(podnikId).collection("Komentare").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    int count = documentSnapshots.size();
                    holder.updateCommentsCount(count);

                } else {

                    holder.updateCommentsCount(0);

                }
            }
        });

        //sucet poctu uzivatelov ktory maju podnik ublubeny
        firebaseFirestore.collection("podnikTester").document(podnikId).collection("Oblubene").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    int count = documentSnapshots.size();
                    holder.updateLikesCount(count);
                    firebaseFirestore.collection("podnikTester").document(podnikId).update("countOblubene", count);

                } else {

                    holder.updateLikesCount(0);
                    firebaseFirestore.collection("podnikTester").document(podnikId).update("countOblubene", 0);

                }
            }
        });

        //nastavanie obrazka oblubenych na hviezdicku prazdnu alebo vyplnenu
            firebaseFirestore.collection("podnikTester").document(podnikId).collection("Oblubene").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            holder.oblubeneO.setImageDrawable(context.getDrawable(R.drawable.hviezda));

                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            holder.oblubeneO.setImageDrawable(context.getDrawable(R.mipmap.ic_star_border_black_24dp));

                        }
                    }
                }
            });

        // set textu na priemer z hodnoteni
        firebaseFirestore.collection("podnikTester").document(podnikId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()) {

                    final Integer suma = (int) (long) task.getResult().getLong("hodnotenie");
                    firebaseFirestore.collection("podnikTester").document(podnikId).collection("hodnotenie").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                            if (!documentSnapshots.isEmpty()) {

                                int count = documentSnapshots.size();
                                holder.updateHodnotenieCount(suma/count,count);

                            } else {

                                holder.updateHodnotenieCount(0, 0);

                            }
                        }
                    });
                } else {

                    holder.updateHodnotenieCount(0,0);

                }
            }
        });

            //pridanie alebo odobranie z oblubenych klinutim na hviezdicku
            holder.oblubeneO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    firebaseFirestore.collection("podnikTester").document(podnikId).collection("Oblubene").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (!task.getResult().exists()) {

                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("podnikTester").document(podnikId).collection("Oblubene").document(currentUserId).set(likesMap);
                               // notifyDataSetChanged();

                            } else {

                                firebaseFirestore.collection("podnikTester").document(podnikId).collection("Oblubene").document(currentUserId).delete();
                              //  notifyDataSetChanged();

                            }
                        }
                    });
                }
            });

            //presmerovanie na jediny podnik
            holder.podnikO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent podnik = new Intent(context, Podnik.class);
                    podnik.putExtra("typPodniku", typ);
                    podnik.putExtra("idPodniku", podnikId);

                    context.startActivity(podnik);

                }
            });

    }



    @Override
    public int getItemCount()  {
        return podnik_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView nazov;
        private TextView telefon;
        private TextView adresa;
        private TextView popis;
        private TextView oblubene;
        private TextView komentare;
        private TextView hodnotenie;

        private ImageView podnikO;
        private ImageView oblubeneO;


        public ViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            oblubeneO = mView.findViewById(R.id.obrazokoblubene);
            podnikO = mView.findViewById(R.id.obrazoksingle);

        }

        public void setDataPodniku(String nazovp, String telefonp, String adesap, String popisp,String podnikOp){

            nazov = mView.findViewById(R.id.nazovsingle);
            telefon = mView.findViewById(R.id.telefonsingle);
            adresa = mView.findViewById(R.id.adresasingle);
            popis = mView.findViewById(R.id.popissingle);
            podnikO = mView.findViewById(R.id.obrazoksingle);
            oblubene = mView.findViewById(R.id.oblubenesingle);

            nazov.setText(nazovp);
            telefon.setText(telefonp);
            adresa.setText(adesap);
            popis.setText(popisp);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.image_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(podnikOp).into(podnikO);

        }

        public void updateHodnotenieCount(int count,int sucet){

            hodnotenie = mView.findViewById(R.id.hodnoteniesingle);
            hodnotenie.setText(count+ " /5 Hodnotenie ("+sucet+")");
        }

        public void updateLikesCount(int count){

            oblubene = mView.findViewById(R.id.oblubenesingle);
            oblubene.setText(count + " Obľúbené");

        }

        public void updateCommentsCount(int count){

            komentare = mView.findViewById(R.id.komentaresingle);
            komentare.setText(count +" Komentárov");

        }

    }

}
