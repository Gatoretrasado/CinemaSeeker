package com.example.ilm.spinnerpersonal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class cine extends Activity{

    static String aux = "reputo";
    static String calleAux = "reputo";
    static String cartAux = "reputo";
    static String nombreCine = "reputo";
    static String maps = "reputo";
    static String salasAux = "reputo";
    static float voto,mediaOld;
    static int votosOld;
    Connection conn;
    Statement st;
    ResultSet rs;
    static String UrlFoto = "https://uranogames.com/protecto/patata.jpg";
    static Bitmap bitmap = null;
    ImageView foto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cine);

        nombreCine = getIntent().getStringExtra("cine");

        sqlThread.start();
        try {
            sqlThread.join();
        } catch (InterruptedException e) {
            System.out.println("Algo salio mal");
        }
        //System.out.println("ESTAS VIVO?"+sqlThread.isAlive());
        bitmap = null;
        new Fotos(cine.this).execute();

        TextView c=(TextView) this.findViewById(R.id.textNombre);
        c.setText(nombreCine);
        TextView calle = (TextView) this.findViewById(R.id.direc);
        calle.setText(calleAux);
        TextView salas = (TextView) this.findViewById(R.id.salas);
        salas.setText("Nº Salas:  "+salasAux);
        RatingBar estrellas = (RatingBar) findViewById(R.id.ratingBar2);
        estrellas.setRating(mediaOld);

        while (bitmap == null) {
            System.out.println("NOOOOOO!!!!");
        }


        calle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(maps);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        TextView cartelera = (TextView)findViewById(R.id.cartelera);
        cartelera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(cartAux);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Button votito = (Button) findViewById(R.id.button2);
        votito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RatingBar votoCine = (RatingBar) findViewById(R.id.ratingBar2);
                voto =  votoCine.getRating();

                new Insertar(cine.this).execute();

                Toast toast2 = Toast.makeText(getApplicationContext(),
                        "Gracias por votar :)", Toast.LENGTH_SHORT);
                toast2.show();
            }
        });

    }

    Thread sqlThread = new Thread() {
        public void run() {
            try {

                Class.forName("com.mysql.jdbc.Driver");
                // "jdbc:mysql://IP:PUERTO/DB", "USER", "PASSWORD");
                // Si estás utilizando el emulador de android y tenes el mysql en tu misma PC no utilizar 127.0.0.1 o localhost como IP, utilizar 10.0.2.2
                 conn = DriverManager.getConnection(
                         "jdbc:mysql://vl19819.dinaserver.com/cinesjose", "cinesjose", "CinesJose12");
                //En el stsql se puede agregar cualquier consulta SQL deseada.
                String stsql = "SELECT `Direccion`,`Salas`,`Media`,`Votos`,`Cartelera`,`Maps`,`image` FROM `cines` WHERE `Nombre` LIKE '" + nombreCine + "'";
                 st = conn.createStatement();
                 rs = st.executeQuery(stsql);
                rs.next();
                //System.out.println( rs.getString(1) );
                cine.calleAux =rs.getString(1)+"";
                cine.salasAux = rs.getInt(2)+"";
                cine.mediaOld =rs.getFloat(3);
                cine.votosOld = rs.getInt(4);
                cine.cartAux =rs.getString(5)+"";
                cine.maps =rs.getString(6)+"";
                cine.UrlFoto = rs.getString(7);

                System.out.println(conn.isClosed());


            } catch (SQLException se) {
                System.out.println("oops! No se puede conectar. Error: " + se.toString());
            } catch (ClassNotFoundException e) {
                System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
            } finally {
                try {
                    conn.close();
                    st.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    };

     private boolean insertar(){

         try {
             float aux;
             int votosNew;

             aux = cine.mediaOld * cine.votosOld;
             aux = aux + cine.voto;
             votosNew = votosOld+1;
             aux = aux / votosNew;

             Class.forName("com.mysql.jdbc.Driver").newInstance();
             // "jdbc:mysql://IP:PUERTO/DB", "USER", "PASSWORD");
             Connection conn1 = DriverManager.getConnection(
                     "jdbc:mysql://vl19819.dinaserver.com/cinesjose", "cinesjose", "CinesJose12");
             // Si estás utilizando el emulador de android y tenes el mysql en tu misma PC no utilizar 127.0.0.1 o localhost como IP, utilizar 10.0.2.2
             //En el stsql se puede agregar cualquier consulta SQL deseada.
             String stsql1 = "UPDATE `cines` SET `Media`="+aux+",`Votos`="+votosNew+" WHERE `Nombre` LIKE'"+nombreCine+"'";
             Statement st1 = conn1.createStatement();
             st1.executeUpdate(stsql1);
             System.out.println(stsql1);
             conn1.close();


         } catch (SQLException se) {
             System.out.println("oops!3 No se puede conectar. Error: " + se.toString());
         } catch (ClassNotFoundException e) {
             System.out.println("oops!3 No se encuentra la clase. Error: " + e.getMessage());
         } catch (InstantiationException e) {
             e.printStackTrace();
         } catch (IllegalAccessException e) {
             e.printStackTrace();
         }
         return false;
     }

    class Insertar extends AsyncTask<String,String,String> {

        private Activity context;

        Insertar(Activity context){
            this.context=context;
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            if(insertar())
                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                    }
                });
            else
                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                    }
                });
            return null;
        }
    }

    private boolean fotos() {

        try {
            java.net.URL url = new java.net.URL(UrlFoto);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);

            foto = (ImageView) findViewById(R.id.foto);
            foto.setImageBitmap(bitmap);
            foto.refreshDrawableState();

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("holi");

        }
        return false;
    }

    class Fotos extends AsyncTask<String, String, String> {

        private Activity context;

        Fotos(Activity context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            if (fotos())
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                    }
                });
            else
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                    }
                });
            return null;
        }
    }

}








