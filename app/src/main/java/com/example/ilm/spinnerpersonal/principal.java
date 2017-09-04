package com.example.ilm.spinnerpersonal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;


public class principal extends Activity{

    String[] ciudades = { "Zaragoza", "Ciudad Real", "Albacete","Cuenca", "Guadalajara" };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);



        final Spinner prueba = (Spinner) findViewById(R.id.spinner);
        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ciudades);

        //Añadimos el layout para el menú
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Le indicamos al spinner el adaptador a usar
        prueba.setAdapter(adapter);


        Button buscar = (Button) findViewById(R.id.buscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckBox vochex = (CheckBox)findViewById(R.id.vo);
                Boolean vo = vochex.isChecked();

                CheckBox opchex = (CheckBox)findViewById(R.id.opera);
                Boolean op = opchex.isChecked();

                CheckBox rechex = (CheckBox)findViewById(R.id.repo);
                Boolean re = rechex.isChecked();

                CheckBox inchex = (CheckBox)findViewById(R.id.inde);
                Boolean in = inchex.isChecked();

                CheckBox corchex = (CheckBox)findViewById(R.id.corto);
                Boolean cor = corchex.isChecked();


                String cadena = "SELECT `Nombre`,`Media` FROM `cines` WHERE `Ciudad` LIKE '"+(prueba.getSelectedItem())+""+"'";

                if(vo){ cadena+=" AND `VO`= 1 ";}
                if(op){ cadena+=" AND `Opera`= 1 ";}
                if(re){ cadena+=" AND `Repo`= 1 ";}
                if(in){ cadena+=" AND `Independiente`= 1 ";}
                if(cor){ cadena+=" AND `Cortos`= 1 ";}

                cadena+= " ORDER BY `Media` DESC";

                Intent List = new Intent(principal.this, MyActivity.class);
                System.out.println(prueba.getSelectedItem());
                List.putExtra("cadena",cadena);
                startActivity(List);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(principal.this);
            builder.setMessage("¿Desconectar?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    FirebaseAuth.getInstance().signOut();
                    Intent meDesconecto = new Intent(principal.this, login.class);
                    meDesconecto.putExtra("disconnet", true);
                    startActivity(meDesconecto);

                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        FirebaseAuth.getInstance().signOut();
        super.onDestroy();
    }

    }
