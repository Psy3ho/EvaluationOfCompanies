package com.example.egoeu.myapplication.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.egoeu.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Pridaj extends AppCompatActivity {

    private ImageView imageView;
    private Uri imageUri = null;
    private Bitmap compressedImageFile;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private String user_id;
    private FirebaseFirestore firebaseFirestore;

    private EditText nazov;
    private EditText popis;
    private EditText telefon;
    private EditText adresa;
    private Button pridajPodnikB;
    private String typPodniku ="";
    private EditText typy;

    private CheckBox checkRestauracia;
    private CheckBox checkPizza;
    private CheckBox checkBar;
    private CheckBox checkPivovar;
    private CheckBox checkJedalen;
    private CheckBox checkKebab;
    private CheckBox checkFastfood;
    private CheckBox checkKaviaren;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pridaj);

        //nech nam nezobrazi hned klavesnicu
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.imageView);
        nazov = findViewById(R.id.nazov);
        popis = findViewById(R.id.popis);
        telefon = findViewById(R.id.telefon);
        adresa = findViewById(R.id.adresa);
        pridajPodnikB =findViewById(R.id.pridajPodnikB);
        checkRestauracia = findViewById(R.id.checkBoxRestauracia);
        checkBar = findViewById(R.id.checkBoxBar);
        checkFastfood = findViewById(R.id.checkBoxFastFood);
        checkJedalen = findViewById(R.id.checkBoxjedalen);
        checkKaviaren = findViewById(R.id.checkBoxkaviaren);
        checkKebab = findViewById(R.id.checkBoxkebab);
        checkPivovar = findViewById(R.id.checkBoxpivovar);
        checkPizza = findViewById(R.id.checkBoxpizza);
        typy = findViewById(R.id.typy);



/*
        //Spinner na vyber typu podniku
        List<String> kategorie = new ArrayList<>();
        kategorie.add("Reštaurácia");
        kategorie.add("Pizzeria");
        kategorie.add("NajHodnotene");
        kategorie.add("Kaviareň");
        kategorie.add("Fast food");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategorie);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typPodniku = typPodniku + adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/



        //pridanie podniku
        pridajPodnikB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkRestauracia.isChecked()){
                    typPodniku = typPodniku +"Reštaurácia, ";
                }
                if(checkPizza.isChecked()){
                    typPodniku = typPodniku +"Pizzeria, ";
                }
                if(checkPivovar.isChecked()){
                    typPodniku = typPodniku +"Piváreň, ";
                }
                if(checkKebab.isChecked()){
                    typPodniku = typPodniku +"kebab, ";
                }
                if(checkKaviaren.isChecked()){
                    typPodniku = typPodniku +"Kaviareň, ";
                }
                if(checkJedalen.isChecked()){
                    typPodniku = typPodniku +"jedáleň, ";
                }
                if(checkFastfood.isChecked()){
                    typPodniku = typPodniku +"Fast food, ";
                }
                if(checkBar.isChecked()){
                    typPodniku = typPodniku +"NajHodnotene, ";
                }
                final String nazovF = nazov.getText().toString();
                final String popisF = popis.getText().toString();
                final String telefonF = telefon.getText().toString();
                final String adresaF = adresa.getText().toString();
                final String typyPodnik = typPodniku + typy.getText().toString();
                Log.d("typy", typyPodnik);





                if(!TextUtils.isEmpty(nazovF)) {

                        if(!TextUtils.isEmpty(popisF)) {

                            if(!TextUtils.isEmpty(adresaF)) {

                                if(telefonF.length()>7) {


                                    //nahranie obrazku do uloziska
                                    final String randomName = UUID.randomUUID().toString();
                                    if( imageUri != null) {
                                        StorageReference imagePath = storageReference.child("fotka_podniku").child(randomName + ".jpg");
                                        imagePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                if (task.isSuccessful()) {

                                                    storeFirestore(task, nazovF, popisF, telefonF, adresaF, typyPodnik);
                                                    Toast.makeText(Pridaj.this, "Obrázok je nahratý ", Toast.LENGTH_LONG).show();

                                                } else {

                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(Pridaj.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();


                                                }
                                            }
                                        });
                                    }else {
                                        Task task =null;
                                        storeFirestore(task, nazovF, popisF, telefonF, adresaF, typyPodnik);
                                    }

                                }else {

                                    Toast.makeText(Pridaj.this, "Napíšte telefónne číslo", Toast.LENGTH_LONG).show();

                                }

                            } else {

                                Toast.makeText(Pridaj.this, "Adresa podniku nieje vyplnená", Toast.LENGTH_LONG).show();

                            }
                        } else {

                            Toast.makeText(Pridaj.this, "Popis podniku nieje vyplnený", Toast.LENGTH_LONG).show();

                        }

                } else {

                    Toast.makeText(Pridaj.this, "Vyplňte názov podniku", Toast.LENGTH_LONG).show();

                }

            }
        });



        //kliknutie na obrazok a pridanie noveho z mobilu volame imagepicker
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(Pridaj.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(Pridaj.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(Pridaj.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }
        });
    }

    //option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pridaj_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.spat:
                startActivity(new Intent(Pridaj.this, MainActivity.class));
                return true;
            default:
                return false;
        }
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String nazovS,String popisS,String telefonS,String adresaS, String typyS) {

        Uri download_uri;
        int countOblubene =0;
        Map<String, Object> podnikMap = new HashMap<>();

        //dany obrazok ktory sme nahrali do uloziska z neho berieme uri adresu a prenesiemu ju do mapy podniku ako string
        if(task != null) {
            download_uri = task.getResult().getDownloadUrl();
            podnikMap.put("image", download_uri.toString());
        } else {
            String url = "https://firebasestorage.googleapis.com/v0/b/bachelorwork-ff8b7.appspot.com/o/fotka_podniku%2Frestauracia.jpg?alt=media&token=85fc5c2b-4a32-4917-b2a8-7f9bc1d329a0";
            podnikMap.put("image", url);
        }
        podnikMap.put("nazov", nazovS);
        podnikMap.put("popis", popisS);
        podnikMap.put("telefon", telefonS);
        podnikMap.put("adresa", adresaS);
        podnikMap.put("typ", typyS.toString());
        podnikMap.put("countOblubene", countOblubene);
        podnikMap.put("hodnotenie",countOblubene);


        //pridanie mapy podniku do databazi aj s udajmi
        firebaseFirestore.collection("podnikTester").add(podnikMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){

                    Toast.makeText(Pridaj.this, "Podnik bol pridaný", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(Pridaj.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(Pridaj.this, "(FIRESTORE Chyba) : " + error, Toast.LENGTH_LONG).show();

                }
            }
        });


    }
    // nahranie obrazku
    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                imageView.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}

