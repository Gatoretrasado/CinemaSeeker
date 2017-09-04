package com.example.ilm.spinnerpersonal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class MyActivity extends Activity implements ListView.OnItemClickListener{

    List<String> ciudadesList = new ArrayList<String>();
    List<String> mediaList = new ArrayList<String>();

    //Array para los nombres
    String[] stringArray;
    //Array para los numeros
    String[] mediaArray;

    int imagenes[] = { R.drawable.toledo, R.drawable.ciudadreal, R.drawable.albacete,
            R.drawable.cuenca, R.drawable.guadalajara};

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView c=(TextView)view.findViewById(R.id.nombre);
        Intent List = new Intent(MyActivity.this, cine.class);
        List.putExtra("cine",c.getText()+"");
        startActivity(List);
    }


    public class AdaptadorPersonalizado extends ArrayAdapter<String> {
        public AdaptadorPersonalizado(Context ctx, int txtViewResourceId, String[] objects){
            super(ctx, txtViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt){
            return crearFilaPersonalizada(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt){
            return crearFilaPersonalizada(pos, cnvtView, prnt);
        }

        public View crearFilaPersonalizada(int position, View convertView, ViewGroup parent){

            LayoutInflater inflater = getLayoutInflater();
            View miFila = inflater.inflate(R.layout.lineaspiner, parent, false);

            TextView nombre = (TextView) miFila.findViewById(R.id.nombre);
            nombre.setText(stringArray[position]);

            TextView media = (TextView) miFila.findViewById(R.id.media);
            media.setText(mediaArray[position]+"/5");

           /* ImageView imagen = (ImageView) miFila.findViewById(R.id.imagenCiudad);
            imagen.setImageResource(imagenes[position]);*/

            return miFila;

        }
    }

    static String cadena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cadena = getIntent().getStringExtra("cadena");

        sqlThread.start();
        try {
            sqlThread.join();
        } catch (InterruptedException e) {
            System.out.println("mierda");
        }

        stringArray = ciudadesList.toArray(new String[ciudadesList.size()]);
        mediaArray = mediaList.toArray(new String[mediaList.size()]);
        setContentView(R.layout.activity_my);
        ListView selectorCiudades = (ListView) findViewById(R.id.list);
        AdaptadorPersonalizado a=new AdaptadorPersonalizado(this, R.layout.lineaspiner, stringArray);
        selectorCiudades.setAdapter(a);
        selectorCiudades.setOnItemClickListener(this);
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
            FirebaseAuth.getInstance().signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    Thread sqlThread = new Thread() {
        public void run() {
            try {

                DecimalFormat df = new DecimalFormat("#.0");
                Class.forName("com.mysql.jdbc.Driver");
                // "jdbc:mysql://IP:PUERTO/DB", "USER", "PASSWORD");
                // Si estás utilizando el emulador de android y tenes el mysql en tu misma PC no utilizar 127.0.0.1 o localhost como IP, utilizar 10.0.2.2
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://vl19819.dinaserver.com/cinesjose", "cinesjose", "CinesJose12");
                //En el stsql se puede agregar cualquier consulta SQL deseada.
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(cadena);
                int num = 1;
                while(rs.next()){
                 System.out.println( rs.getString(1) );
                 ciudadesList.add(rs.getString(1));
                    mediaList.add(df.format(rs.getFloat(2))+"");
                 num++;
                }
                conn.close();

            } catch (SQLException se) {
                System.out.println("oops! No se puede conectar. Error: " + se.toString());
            } catch (ClassNotFoundException e) {
                System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
            }
        }
    };
}
