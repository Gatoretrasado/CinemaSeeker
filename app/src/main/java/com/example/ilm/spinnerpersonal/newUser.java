package com.example.ilm.spinnerpersonal;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class newUser extends Activity{

    EditText txtNuevoCorreo;


    EditText txtNuevaPass;


    EditText txtNuevaPass2;

    private FirebaseAuth mAuth;
    public ProgressDialog progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newuser);

        mAuth = FirebaseAuth.getInstance();


        txtNuevoCorreo = (EditText)findViewById(R.id.txtNuevoCorreo);
        txtNuevaPass = (EditText)findViewById(R.id.txtNuevaPass);
        txtNuevaPass2 = (EditText)findViewById(R.id.txtNuevaPass2);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Button  aceptar = (Button)findViewById(R.id.btnAceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NUEVO_USUARIO();
            }
        });

        Button  cancelar = (Button)findViewById(R.id.btnCancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CANCELAR();
            }
        });

    }

    public void NUEVO_USUARIO() {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        String NCorreo = txtNuevoCorreo.getText().toString();
        String NPass1 = txtNuevaPass.getText().toString();
        String NPass2 = txtNuevaPass2.getText().toString();

        if (VALIDATE(NCorreo, NPass1, NPass2)) {
            progreso = ProgressDialog.show(this, "", "Creando Usuario", true);
            mAuth.createUserWithEmailAndPassword(NCorreo, NPass1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(newUser.this, "Ya eres uno de los nuestos!", Toast.LENGTH_LONG).show();
                        progreso.dismiss();
                        finish();
                    }

                    if (!task.isSuccessful()) {
                        progreso.dismiss();
                        Log.w("ERROR", "ERROR CREAR", task.getException());
                        Toast.makeText(newUser.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean VALIDATE(String NCorreo, String NPass1, String NPass2) {
        int todoOK = 0;

        if (NCorreo.isEmpty() || NCorreo.length() == 0) {
            txtNuevoCorreo.setError("Rellene el campo");
            todoOK += 0;
        } else {
            txtNuevoCorreo.setError(null);
            todoOK += 1;

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(NCorreo).matches()) {
                txtNuevoCorreo.setError("Formato Invalido");
                todoOK += 0;
            } else {
                txtNuevoCorreo.setError(null);
                todoOK += 1;
            }
        }

        if (NPass1.isEmpty() || NPass1.length() == 0) {
            txtNuevaPass.setError("Rellene el campo");
            todoOK += 0;
        } else {
            txtNuevaPass.setError(null);
            todoOK += 1;

            if (NPass1.length() < 6) {
                txtNuevaPass.setError("6 Caracteres Min.");
                todoOK += 0;
            } else {
                txtNuevaPass.setError(null);
                todoOK += 1;
            }
        }

        if (NPass2.isEmpty() || NPass2.length() == 0) {
            txtNuevaPass2.setError("Rellene el campo");
            todoOK += 0;
        } else {
            txtNuevaPass2.setError(null);
            todoOK += 1;

            if (!NPass2.equals(NPass1)) {
                txtNuevaPass2.setError("Las contraseñas no coinciden");
                todoOK += 0;
            } else {
                txtNuevaPass2.setError(null);
                todoOK += 1;
            }
        }


        return todoOK == 6;
    }

    public void CANCELAR() {
        finish();
    }

}
