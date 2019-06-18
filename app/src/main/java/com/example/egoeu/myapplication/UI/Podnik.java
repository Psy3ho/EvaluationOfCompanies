package com.example.egoeu.myapplication.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.egoeu.myapplication.R;
import com.example.egoeu.myapplication.Adapters.KomentarRecyclerAdapter;
import com.example.egoeu.myapplication.Cards.CardKomentar;
import com.example.egoeu.myapplication.Fragments.Koment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Podnik extends AppCompatActivity implements OnMapReadyCallback {

    public Context context;
    private FirebaseFirestore firebaseFirestore;

    private TextView nazov;
    private TextView telefon;
    private TextView adresa;
    private TextView popis;
    private TextView oblubene;
    private TextView komentare;

    private ImageView podnikO;
    private ImageView oblubeneO;
    private ImageView komentareO;
    private ImageView telefonO;
    private ImageView lokacia;
    private GoogleMap map;


    private TextView komentarW;
    private Koment koment;

    private RatingBar ratingBar;
    private TextView hodnotenie;


    private FirebaseAuth mAuth;

    private List<CardKomentar> komentarList;
    private RecyclerView komentare_view_list;
    private KomentarRecyclerAdapter komentarRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podnik);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        nazov = findViewById(R.id.nazovsingle);
        telefon = findViewById(R.id.telefonsingle);
        adresa = findViewById(R.id.adresasingle);
        popis = findViewById(R.id.popissingle);
        podnikO = findViewById(R.id.obrazoksingle);
        oblubeneO = findViewById(R.id.obrazokoblubene);
        telefonO = findViewById(R.id.telefonB);
        lokacia = findViewById(R.id.lokacia);

        ratingBar = findViewById(R.id.ratingBar);
        hodnotenie = findViewById(R.id.hodnotenie);

        komentarW = findViewById(R.id.komentarW);
        komentareO = findViewById(R.id.obrazokkomentare);
        komentare = findViewById(R.id.komentaresingle);

        //z adapteru ktory odkazuje na dany typ podniku
        final String typ = getIntent().getStringExtra("typPodniku");
        final String podnikId = getIntent().getStringExtra("idPodniku");
        mAuth = FirebaseAuth.getInstance();

        final String currentUserId = mAuth.getCurrentUser().getUid();
        final String currentUserEmail = mAuth.getCurrentUser().getEmail().toString();

        if (mAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            //vyberieme z databazky dany podnik
            firebaseFirestore.collection("podnikTester").document(podnikId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        String nazov = task.getResult().getString("nazov");
                        String telefon = task.getResult().getString("telefon");
                        String adresa = task.getResult().getString("adresa");
                        String popis = task.getResult().getString("popis");
                        String podnikO = task.getResult().getString("image");
                        geoLocate(adresa, nazov);
                        setDataPodniku(nazov, telefon, adresa, popis, podnikO);


                    } else {
                        //Firebase Exception
                    }
                }
            });

            //inicializacia fragmentu s komentarmi
            koment = new Koment();
            initializeFragment(typ, podnikId);
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.komentar_container);

            //fragment na mapku
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapa);
            mapFragment.getMapAsync(Podnik.this);

            //listener ak klikneme na dresu posle nas na dalsiu appku ktora urcuje polohu nech ju vyhlada na mape
            adresa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="
                            + adresa.getText().toString()));
                    startActivity(geoIntent);
                }
            });
            lokacia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="
                            + adresa.getText().toString()));
                    startActivity(geoIntent);
                }
            });

            //ak klikneme na telefonne cislo alebo telefon posle nas na vytacanie
            telefonO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    String phone_no = telefon.getText().toString().trim();
                    intent.setData(Uri.parse("tel:" + phone_no));
                    if (ActivityCompat.checkSelfPermission(Podnik.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);

                }
            });
            telefon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    String phone_no = telefon.getText().toString().trim();
                    intent.setData(Uri.parse("tel:" + phone_no));
                    if (ActivityCompat.checkSelfPermission(Podnik.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);
                }
            });


        }




        //z databazky podla typu podniku si vytiahneme kto ma podnik oblubeny a tento pocet ukazeme uzivatelovi a zaroven aktualizujeme tuto informaciu v podniku
        firebaseFirestore.collection("podnikTester").document(podnikId).collection("Oblubene").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    int count = documentSnapshots.size();
                    updateLikesCount(count);
                    firebaseFirestore.collection("podnikTester").document(podnikId).update("countOblubene",count);

                } else {

                    updateLikesCount(0);
                    firebaseFirestore.collection("podnikTester").document(podnikId).update("countOblubene",0);

                }

            }
        });

        //z databazky podla podniku vztiahneme komentare a zistime ich pocet
        firebaseFirestore.collection("podnikTester").document(podnikId).collection("Komentare").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    int count = documentSnapshots.size();

                    updateCommentsCount(count);

                } else {

                    updateCommentsCount(0);

                }
            }
        });


        //vytiahneme dany podnik a jeho sumu hodnoteni pottom cez tento podnik nacrieme do databazy znovu vytiahneme
        // u daneho podniku celu kolekciu hodnotení a zistime jej pocet podla toho spravime aritmeticky priemer sucet hodnotení delene pocet hodnoteni
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
                                hodnotenie.setText(suma / count +" / 5 Hodnotenie (" + count + ")");
                                updateLikesCount(count);
                            } else {
                                hodnotenie.setText(0 +" / 5 Hodnotenie (" + 0 + ")");

                            }

                        }
                    });
                } else {
                    hodnotenie.setText(0 +" / 5 Hodnotenie (" + 0 + ")");

                }

            }
        });


        /*//nove hodnotenie podniku
         klinutim na rating bar zavolame z databzi podla dany podniku jeho kolekciu hodnoteni a daneho uzivatela
         a zistime ci uz podnik hodnotil alebo nie v pripade ze nie

         False = vlozime na do mapy pocet hviezdiciek a vlozime do do databzi na hodnotenie
            zaroven podla zasa zavolame databazu a podnik zistime sumu hodnotení a konej pripocitame pocet hviezdiciek
            aktualizujeme sumu v databaze potom znova zavolame databazu a kolekciu hodnoteni ak nieje prazdna tak aktualizujeme text
            priemer hodnotení

         v pripade ze hej
         True = vytiahenme si najprv pocet hviezdiciek ktore uz uzivatel udelil podniku
            vlozime na do mapy novy pocet hviezdiciek a vlozime do do databazi na hodnotenie
            zaroven podla zasa zavolame databazu a podnik zistime sumu hodnotení a suma = suma -stare hodnotenie + nove hodnotenie
            aktualizujeme sumu v databaze potom znova zavolame databazu a kolekciu hodnoteni ak nieje prazdna tak aktualizujeme text
            priemer hodnotení
          */
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float v, boolean b) {
                final int i = Math.round(v);
                firebaseFirestore.collection("podnikTester").document(podnikId).collection("hodnotenie").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()) {

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("pocet", i);

                            firebaseFirestore.collection("podnikTester").document(podnikId).collection("hodnotenie").document(currentUserId).set(likesMap);
                            firebaseFirestore.collection("podnikTester").document(podnikId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    Integer suma = (int) (long) task.getResult().getLong("hodnotenie");
                                    suma = suma +i;

                                    firebaseFirestore.collection("podnikTester").document(podnikId).update("hodnotenie", suma);

                                    final int finall =suma;

                                    firebaseFirestore.collection("podnikTester").document(podnikId).collection("hodnotenie").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                            if (!documentSnapshots.isEmpty()) {

                                                int count = documentSnapshots.size();
                                                hodnotenie.setText(finall / count +" / 5 Hodnotenie (" + count + ")");
                                                updateLikesCount(count);

                                            } else {

                                                hodnotenie.setText(0 +" / 5 Hodnotenie (" + 0 + ")");

                                            }
                                        }
                                    });
                                }
                            });
                        } else {

                            final Integer hodnotenie1 = (int) (long) task.getResult().getLong("pocet");
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("pocet", i);

                            firebaseFirestore.collection("podnikTester").document(podnikId).collection("hodnotenie").document(currentUserId).set(likesMap);
                            firebaseFirestore.collection("podnikTester").document(podnikId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    Integer suma = (int) (long) task.getResult().getLong("hodnotenie");
                                    suma = suma -hodnotenie1 +i;

                                    firebaseFirestore.collection("podnikTester").document(podnikId).update("hodnotenie", suma);

                                    final Integer finalll = suma;

                                    firebaseFirestore.collection("podnikTester").document(podnikId).collection("hodnotenie").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                            if (!documentSnapshots.isEmpty()) {

                                                int count = documentSnapshots.size();
                                                hodnotenie.setText(finalll / count +" / 5 Hodnotenie (" + count + ")");
                                                updateLikesCount(count);

                                            } else {

                                                hodnotenie.setText(0 +" / 5 Hodnotenie (" + 0 + ")");

                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });

        // vytiahneme z databazky podla podniku a uzivatela kolko uz udelil hviezdiciek a to ukazeme na rating baru
        firebaseFirestore.collection("podnikTester").document(podnikId).collection("hodnotenie").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        Integer hodnotenie = (int) (long) task.getResult().getLong("pocet");
                        ratingBar.setRating(hodnotenie);

                    }else{

                        ratingBar.setRating(0);
                    }
                } else {
                    //Firebase Exception
                }
            }
        });


        //Zmena farby oblebeneho podniku bud zlata hviezdicka alebo len s ciernym okrajom
        firebaseFirestore.collection("podnikTester").document(podnikId).collection("Oblubene").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        oblubeneO.setImageDrawable(getDrawable(R.drawable.hviezda));
                    }

                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        oblubeneO.setImageDrawable(getDrawable(R.mipmap.ic_star_border_black_24dp));
                    }
                }
            }
        });

        //pridanie podniku do oblubenych daneho pouzivatela
        oblubeneO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("podnikTester").document(podnikId).collection("Oblubene").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()) {

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("podnikTester").document(podnikId).collection("Oblubene").document(currentUserId).set(likesMap);

                        } else {

                            firebaseFirestore.collection("podnikTester").document(podnikId).collection("Oblubene").document(currentUserId).delete();

                        }
                    }
                });
            }
        });

        //odoslanie komentara a pridanie do databázi
        komentareO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String komentarF = komentarW.getText().toString();

                if (!TextUtils.isEmpty(komentarF)) {

                    Map<String, Object> komentarMap = new HashMap<>();
                    komentarMap.put("email", currentUserEmail);
                    komentarMap.put("komentar", komentarF);
                    komentarMap.put("podnik_id", podnikId);
                    komentarMap.put("user_id", currentUserId);
                    komentarMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("podnikTester").document(podnikId).collection("Komentare").add(komentarMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(Podnik.this, "Komentár odoslaný", Toast.LENGTH_LONG).show();
                                komentarW.setText("");
                                //schovanie klavesnice po pridaní komentara
                                InputMethodManager inputManager = (InputMethodManager)
                                        getSystemService(INPUT_METHOD_SERVICE);
                                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);

                            } else {

                                String error = task.getException().getMessage();

                            }
                        }
                    });
                } else {

                    Toast.makeText(Podnik.this, "Napíšte komentár", Toast.LENGTH_LONG).show();

                }
            }
        });
    }



    //menu na vratenie sa spet do mainu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.pridaj_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.spat:
                startActivity(new Intent(Podnik.this, MainActivity.class));
                return true;
            default:
                return false;

        }
    }


    //nastavaneie dat pre zobrazenie atributov podniku uzivatelu v aktivite
    public void setDataPodniku(String nazovp, String telefonp, String adesap, String popisp,String podnikOp){

        nazov = findViewById(R.id.nazovsingle);
        telefon = findViewById(R.id.telefonsingle);
        adresa = findViewById(R.id.adresasingle);
        popis = findViewById(R.id.popissingle);
        podnikO = findViewById(R.id.obrazoksingle);

        nazov.setText(nazovp);
        telefon.setText(telefonp);
        adresa.setText(adesap);
        popis.setText(popisp);

        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption = placeholderOption.placeholder(R.drawable.image_placeholder);

        Glide.with(Podnik.this).applyDefaultRequestOptions(placeholderOption).load(podnikOp).into(podnikO);

    }

    //aktualizacia kolky maju podnik v oblbuenych
    public void updateLikesCount(int count) {

        oblubene = findViewById(R.id.oblubenesingle);
        oblubene.setText(count + " Obľúbené");

    }

    //aktualizacia kolko komentarov ma podnik
    public void updateCommentsCount(int count){
        komentare = findViewById(R.id.komentaresingle);
        komentare.setText(count +" Komentárov");
    }

    //inicializacia fragmentu na komentare
    private void initializeFragment(String typp, String podnikIdp) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle =new Bundle();
        bundle.putString("typk",typp);
        bundle.putString("podnikIdk",podnikIdp);
        fragmentTransaction.add(R.id.komentar_container, koment);
        koment.setArguments(bundle);
        fragmentTransaction.commit();

    }

    //zobrazenie na mape podla adresy a tiez znacku s nazvom podniku
    private void geoLocate(String adresapa,String nazovpa){
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        double lat= 0.0, lng= 0.0;
        try {
            List<Address> addresses = (List<Address>) gc.getFromLocationName(adresapa,1);
            if (addresses.size() > 0)
            {
                lat=addresses.get(0).getLatitude();
                lng=addresses.get(0).getLongitude();
                LatLng sydney = new LatLng(lat, lng);

                map.addMarker(new MarkerOptions().position(sydney).title(nazovpa));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map= googleMap;

    }
}
