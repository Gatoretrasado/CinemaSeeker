package com.example.ilm.spinnerpersonal;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class olvida extends Activity{


    EditText ResetCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.olvida);

        ResetCorreo = (EditText)findViewById(R.id.txtCorreoOlvidado);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Button reset = (Button)findViewById(R.id.btnPassReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Enviar_Correo();
            }
        });
    }


    public void Enviar_Correo() {

        String RCorreo = ResetCorreo.getText().toString();

        if (VALIDATE(RCorreo)) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(RCorreo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(olvida.this, "Mira tu correo melon", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(olvida.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public boolean VALIDATE(String RCorreo) {
        int todoOK = 0;

        if (RCorreo.isEmpty() || RCorreo.length() == 0) {
            ResetCorreo.setError("Rellene el campo");
            todoOK += 0;
        } else {
            ResetCorreo.setError(null);
            todoOK += 1;

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(RCorreo).matches()) {
                ResetCorreo.setError("Formato Invalido");
                todoOK += 0;
            } else {
                ResetCorreo.setError(null);
                todoOK += 1;
            }
        }

        return todoOK == 2;
    }
}
