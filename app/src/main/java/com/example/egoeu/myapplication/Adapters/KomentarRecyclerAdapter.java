package com.example.egoeu.myapplication.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.egoeu.myapplication.R;
import com.example.egoeu.myapplication.Cards.CardKomentar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

/**
 * Created by egoeu on 7. 4. 2018.
 */

public class KomentarRecyclerAdapter extends RecyclerView.Adapter<KomentarRecyclerAdapter.ViewHolder>{

    public List<CardKomentar> komentarList;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public KomentarRecyclerAdapter(List<CardKomentar> komentarList){

        this.komentarList = komentarList;

    }

    @Override
    public KomentarRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull KomentarRecyclerAdapter.ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String userEmail = komentarList.get(position).getEmail();
        final String komentar = komentarList.get(position).getKomentar();
        final String podnikId = komentarList.get(position).getPodnik_id();
        final String komentarId = komentarList.get(position).CardKomentarId;

        Log.d(komentar,userEmail);

        //nastavenie komentara text a email
        holder.setDataKomentara(komentar,userEmail);
        holder.setKrizik(userEmail);



        //cas pridania komentara
        try {

            long dateString = komentarList.get(position).getTimestamp().getTime();
            final String cas = DateFormat.format("MM/dd/yyyy hh:mm:ss", new Date(dateString)).toString();
            Log.d("datum ",cas);
            holder.setDataCas(cas);

        }catch (Exception e) {
        }

        //vymazanie komentara aj s dialogovým oknom
        holder.krizik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                firebaseFirestore.collection("podnikTester").document(podnikId).collection("Komentare").document(komentarId).delete();
                                komentarList.remove(position);
                                notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Vymazať komentár?").setPositiveButton("Áno", dialogClickListener)
                        .setNegativeButton("Nie", dialogClickListener).show();

            }
        });



    }

    @Override
    public int getItemCount() {
        return komentarList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView email;
        private TextView komentar;
        private TextView cas;

        private ImageView krizik;

        public ViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

        }

        public void setDataKomentara(String komentarp,String emialp) {

            email = mView.findViewById(R.id.emailsingle);
            komentar = mView.findViewById(R.id.komentaresingle);
            email.setText(emialp);
            komentar.setText(komentarp);

        }

        public void setKrizik(String userEmail){
            krizik = mView.findViewById(R.id.krizikO);
            if(userEmail.equals(firebaseAuth.getCurrentUser().getEmail().toString())){
                krizik.setVisibility(View.VISIBLE);
            }
        }


        public void setDataCas(String datump) {

            cas = mView.findViewById(R.id.timesingle);
            cas.setText(datump);
        }
    }
}
