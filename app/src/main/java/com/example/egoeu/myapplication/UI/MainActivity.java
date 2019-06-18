package com.example.egoeu.myapplication.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;

import com.example.egoeu.myapplication.R;
import com.example.egoeu.myapplication.Fragments.NajHodnotene;
import com.example.egoeu.myapplication.Fragments.FastFood;
import com.example.egoeu.myapplication.Fragments.Hladanie;
import com.example.egoeu.myapplication.Fragments.Kava;
import com.example.egoeu.myapplication.Fragments.Pizza;
import com.example.egoeu.myapplication.Fragments.Restauracie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private StorageReference storage;
    private FirebaseUser mCurrentUser;
    private String current_user_id;

    private NajHodnotene najHodnotene;
    private FastFood fastFood;
    private Kava kava;
    private Pizza pizza;
    private Restauracie restauracie;
    private Hladanie hladanie;

    private BottomNavigationView mainbottomNav;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ak nieje pripojeny mobil k netu
        if(!isConnected(MainActivity.this))buildDialog(MainActivity.this).show();
        else {
            //Log.d("internet","stale sme na sieti");
            setContentView(R.layout.activity_main);


            mAuth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance().getReference();
            mainbottomNav = findViewById(R.id.mainBottomNav);


            if (mAuth.getCurrentUser() != null) {
                najHodnotene = new NajHodnotene();
                fastFood = new FastFood();
                kava = new Kava();
                pizza = new Pizza();
                restauracie = new Restauracie();
                hladanie = new Hladanie();

                initializeFragment();

                //menu na spodku na prepinanie fragmentov podla typu podniku
                mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                        switch (item.getItemId()) {

                            case R.id.restauracia:

                                replaceFragment(restauracie, currentFragment);
                                return true;

                            case R.id.pizza:

                                replaceFragment(pizza, currentFragment);
                                return true;

                            case R.id.bar:

                                replaceFragment(najHodnotene, currentFragment);
                                return true;

                            case R.id.kaviaren:

                                replaceFragment(kava, currentFragment);
                                return true;

                            case R.id.fastfood:

                                replaceFragment(fastFood, currentFragment);
                                return true;

                            default:
                                return false;

                        }

                    }
                });

            }
        }
    }


    @Override
    protected void onStart() {

        super.onStart();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser == null) {

            startActivity(new Intent(MainActivity.this, Login.class));
            finish();

        } else {
            if(isConnected(MainActivity.this)) {

                current_user_id = mAuth.getCurrentUser().getUid();
            }

        }
    }

    //hlavne menu + vyhladavanie v menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;

            }

            @Override
            public boolean onQueryTextChange(String s) {

                Bundle bundle = new Bundle();
                Log.d("nieco co hladame : ",s);
                bundle.putString("hladat", s);
                hladanie.setArguments(bundle);
                initializeFragmentHladanie();
                return false;
            }
        });
        return true;
    }

    //polozka vybrata z menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,Login.class));
                finish();
                return true;
            case R.id.action_add:
                startActivity(new Intent(MainActivity.this, Pridaj.class));
                return true;
            case R.id.search:
                return true;
            case R.id.oblubene:
                startActivity(new Intent(MainActivity.this, Oblubene.class));
                return true;
            default:
                return false;
        }
    }


    //nacitanie fragmentov a zobrazenie jedneho ostatne skryjeme
    private void initializeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, najHodnotene);
        fragmentTransaction.add(R.id.main_container, fastFood);
        fragmentTransaction.add(R.id.main_container, restauracie);
        fragmentTransaction.add(R.id.main_container, pizza );
        fragmentTransaction.add(R.id.main_container, kava);
        fragmentTransaction.hide(kava);
        fragmentTransaction.hide(restauracie);
        fragmentTransaction.hide(fastFood);
        fragmentTransaction.hide(pizza);
        fragmentTransaction.hide(hladanie);
        fragmentTransaction.commit();
    }

    private void initializeFragmentHladanie(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        i++;

        if(i>1){

            fragmentTransaction.detach(hladanie);
            fragmentTransaction.attach(hladanie);
            fragmentTransaction.show(hladanie);
            fragmentTransaction.hide(kava);
            fragmentTransaction.hide(restauracie);
            fragmentTransaction.hide(najHodnotene);
            fragmentTransaction.hide(fastFood);
            fragmentTransaction.hide(pizza);

        } else {

             fragmentTransaction.add(R.id.main_container,hladanie);
             fragmentTransaction.detach(hladanie);
             fragmentTransaction.attach(hladanie);
             fragmentTransaction.show(hladanie);

        }
        //debug
        Log.d("cislo   " ,"blkljblk    "+i);

        fragmentTransaction.commit();

    }

    //prehodenie zobrazovaneho fragmentu
    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == pizza){
//            fragmentTransaction.replace(R.id.main_container,pizza);

            fragmentTransaction.hide(kava);
            fragmentTransaction.hide(restauracie);
            fragmentTransaction.hide(najHodnotene);
            fragmentTransaction.hide(fastFood);
            fragmentTransaction.hide(hladanie);
        }

        if(fragment == kava){
 //           fragmentTransaction.replace(R.id.main_container,kava);

            fragmentTransaction.hide(restauracie);
            fragmentTransaction.hide(najHodnotene);
            fragmentTransaction.hide(fastFood);
            fragmentTransaction.hide(pizza);
            fragmentTransaction.hide(hladanie);
        }

        if(fragment == restauracie){
 //           fragmentTransaction.replace(R.id.main_container,restauracie);

            fragmentTransaction.hide(kava);
            fragmentTransaction.hide(najHodnotene);
            fragmentTransaction.hide(fastFood);
            fragmentTransaction.hide(pizza);
            fragmentTransaction.hide(hladanie);
        }

        if(fragment == najHodnotene){
//            fragmentTransaction.replace(R.id.main_container,najHodnotene);

            fragmentTransaction.hide(kava);
            fragmentTransaction.hide(restauracie);
            fragmentTransaction.hide(fastFood);
            fragmentTransaction.hide(pizza);
            fragmentTransaction.hide(hladanie);
        }

        if(fragment == fastFood){
 //           fragmentTransaction.replace(R.id.main_container,fastFood);

            fragmentTransaction.hide(kava);
            fragmentTransaction.hide(restauracie);
            fragmentTransaction.hide(najHodnotene);
            fragmentTransaction.hide(pizza);
            fragmentTransaction.hide(hladanie);
        }
        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }

    //metody ci sme pripojeny na internet
    public boolean isConnected(MainActivity context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {

            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
        else return false;
        } else
        return false;
    }

    public AlertDialog.Builder buildDialog(MainActivity c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Žiadne internetové pripojenie");
        builder.setMessage("Potrebujete wi-fi alebo internetovú mobilnú sieť.");

        builder.setPositiveButton("Zavrieť", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
                return;
            }
        });

        return builder;
    }








}
