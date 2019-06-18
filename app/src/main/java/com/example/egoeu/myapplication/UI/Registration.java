package com.example.egoeu.myapplication.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.egoeu.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Registration extends AppCompatActivity  implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText emailR, hesloR, hesloR2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mAuth = FirebaseAuth.getInstance();
        emailR = findViewById(R.id.emailR);
        hesloR = findViewById(R.id.hesloR);
        hesloR2 = findViewById(R.id.hesloR2);
    }

     public void onClick(View v) {

    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            sendToMain();

        }

    }
    private void sendToMain() {

        Intent mainIntent = new Intent(Registration.this, Login.class);
        startActivity(mainIntent);
        finish();

    }

    public void registracia(View view) {

        String email =  emailR.getText().toString();
        String heslo = hesloR.getText().toString();
        String heslo2 = hesloR2.getText().toString();

        if (TextUtils.isEmpty(email)) {

            Toast.makeText(getApplicationContext(), "Napíšte email", Toast.LENGTH_SHORT).show();
            return;

        }

        if (TextUtils.isEmpty(heslo)||TextUtils.isEmpty(heslo2)) {

            Toast.makeText(getApplicationContext(), "Napíšte heslo!", Toast.LENGTH_SHORT).show();
            return;

        }

        if (heslo== heslo2) {

            Toast.makeText(getApplicationContext(), "heslo sa musí zhodovať!", Toast.LENGTH_SHORT).show();
            return;

        }

        if (hesloR.length() < 6) {

            Toast.makeText(getApplicationContext(), "Heslo mmusí obsahovať aspoň 6 znakov!", Toast.LENGTH_SHORT).show();
            return;

        }

        mAuth.createUserWithEmailAndPassword(email, heslo)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            startActivity(new Intent(Registration.this, MainActivity.class));

                        } else {

                            Toast.makeText(Registration.this, "Účet už existuje", Toast.LENGTH_SHORT).show();

                        }
                    }


                });
    }

    public void Prihlasenie(View view) {
        startActivity(new Intent(Registration.this,Login.class));
    }



}
