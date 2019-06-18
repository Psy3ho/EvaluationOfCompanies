package com.example.egoeu.myapplication.UI;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.egoeu.myapplication.R;
import com.example.egoeu.myapplication.Fragments.OblubeneFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Oblubene extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private StorageReference storage;
    private DatabaseReference databaseRef, mDatabaseUsers;
    private FirebaseUser mCurrentUser;

    private String current_user_id;

    private OblubeneFragment oblubeneFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oblubene);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        if(mAuth.getCurrentUser() != null) {

            oblubeneFragment = new OblubeneFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.oblubene_container, oblubeneFragment);
            fragmentTransaction.detach(oblubeneFragment);
            fragmentTransaction.attach(oblubeneFragment);
            fragmentTransaction.commit();

        }
    }

    //menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pridaj_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.spat:
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.detach(oblubeneFragment);
                fragmentTransaction.commit();
                finish();
                startActivity(new Intent(Oblubene.this, MainActivity.class));
                return true;
            default:
                return false;
        }
    }
}
