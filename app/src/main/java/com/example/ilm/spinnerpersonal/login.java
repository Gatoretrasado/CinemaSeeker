package com.example.ilm.spinnerpersonal;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;


public class login extends Activity {

    EditText txtCorreo ;
    EditText txtPass ;


    public ProgressDialog progreso;
    private SharedPreferences preferencias;
    private boolean desconectado = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getToken();

        mAuth = FirebaseAuth.getInstance();

        txtCorreo = (EditText) findViewById(R.id.txtCorreo);
        txtPass = (EditText) findViewById(R.id.txtPassword);
        preferencias = getSharedPreferences("checkRecordar", Context.MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            desconectado = extras.getBoolean("disconnet", true);

            SharedPreferences.Editor editor = preferencias.edit();
            editor.putBoolean("recordar", false);
            editor.apply();
        }


        if ( !desconectado) {
            //Cargamos el ultimo email, pass y estado de recordar
            txtCorreo.setText(preferencias.getString("correo", null));
            txtPass.setText(preferencias.getString("pass", null));
            Abrimos_Main();
        } else {
            txtCorreo.setText(preferencias.getString("correo", null));
            txtPass.setText("");
        }

        Button loginBtn = (Button)findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Abrimos_Main();
            }
        });

        TextView olvBtn = (TextView)findViewById(R.id.lblOlvidado);
        olvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), olvida.class);
                startActivity(intent);
            }
        });

        TextView newBtn = (TextView)findViewById(R.id.lblNuevoUser);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), newUser.class);
                startActivity(intent);
            }
        });
    }


    public void Abrimos_Main() {
        String Correo = txtCorreo.getText().toString();
        String Pass = txtPass.getText().toString();
        VALIDATE(Correo, Pass);
        //Si algun campo esta vacio mostramos error
        if (VALIDATE(Correo, Pass)) {
            progreso = ProgressDialog.show(this, "", "Conectando...", true);

            mAuth.signInWithEmailAndPassword(Correo, Pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Guaramos el correo, la contraseña para no tener que volverlo a escribir
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putString("correo", txtCorreo.getText().toString());
                        editor.putString("pass", txtPass.getText().toString());
                        editor.putBoolean("recordar", true);
                        editor.apply();

                        Intent intent = new Intent(getBaseContext(), principal.class);
                        startActivity(intent);
                        progreso.dismiss();
                        finish();
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progreso.dismiss();
                    Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public boolean VALIDATE(String Correo, String Pass) {
        int todoOK = 0;

        if (Correo.isEmpty() || Correo.length() == 0) {
            txtCorreo.setError("Rellene el campo");
            todoOK += 0;
        } else {
            txtCorreo.setError(null);
            todoOK += 1;

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Correo).matches()) {
                txtCorreo.setError("Formato Invalido");
                todoOK += 0;
            } else {
                txtCorreo.setError(null);
                todoOK += 1;
            }
        }

        if (Pass.isEmpty() || Pass.length() == 0) {
            txtPass.setError("Rellene el campo");
            todoOK += 0;
        } else {
            txtPass.setError(null);
            todoOK += 1;
        }

        return todoOK == 3;
    }


}
