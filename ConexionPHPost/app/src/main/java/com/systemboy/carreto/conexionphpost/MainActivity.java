package com.systemboy.carreto.conexionphpost;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            //Iniciamos el fragmento
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholdeFragment())
                    .commit();
        }
    }

    public void sendNewArticle(Articles newArticle){
        //obtenemos los datos ddel Articles en formato JSON
        String strJson = newArticle.toJSON();
        //URL
        String baseUrl = "http://192.168.1.70/Android/recibir.php";
        //Se ejecuta la peticion Http POST empleando AsyncTask
        new MyHttpPostRequest().execute(baseUrl,strJson);

    }

    public void processResult(String result){
        if(result.contains("OK")){
            Toast.makeText(getApplicationContext(),"Vientos!!!",
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Sorry Bro!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragmento
     */
    public static class PlaceholdeFragment extends Fragment {
        public TextView titleTextView;
        public EditText titleEditText;
        public TextView urlTextView;
        public EditText urlEditText;
        public TextView descTextView;
        public EditText descEditText;
        public Button btnSendData;

        public PlaceholdeFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_article, container, false);

            this.titleEditText = (EditText) rootView.findViewById(R.id.nombre);
            this.titleEditText.setText("Titulo del Articulo");
            this.urlEditText = (EditText) rootView.findViewById(R.id.link);
            this.urlEditText.setText("http://expocodetech.com");
            this.descEditText = (EditText) rootView.findViewById(R.id.descripcion);
            this.descEditText.setText("Descrip del articulo");
            this.btnSendData = (Button) rootView.findViewById(R.id.btnEnviar);
//Definimos un ClickListener para le Boton de envio de datos
            this.btnSendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//Obtener los datos introducidos por el
//usuario y realizar la peticion HTTP POST
                    processScreen();
                }
            });
            return rootView;
        }

        private void processScreen(){
//Obtenemos los datos insertados por el usuario en pantalla
            String pTitle = this.titleEditText.getText().toString();
            String pDesc = this.descEditText.getText().toString();
            String pUrl = this.urlEditText.getText().toString();

//Instanciamos el objeto Articles con los datos insertados por el usuario
            Articles newArticle = new Articles(pTitle, pDesc, pUrl);
//Llamamos la método sendNewArticle definido en el MainActivity
//para Enviar los datos al servidor
            ((MainActivity)getActivity()).sendNewArticle(newArticle);
        }
    }
    /**
     * MyHttpPostRequest => Esta clase privada maneja toda la gestión de envío de datos al servidor
     *
     */
    private class MyHttpPostRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            BufferedReader in = null;
            String baseUrl = params[0];
            String jsonData = params[1];

            try {
//Creamos un objeto Cliente HTTP para manejar la peticion al servidor
                HttpClient httpClient = new DefaultHttpClient();
//Creamos objeto para armar peticion de tipo HTTP POST
                HttpPost post = new HttpPost(baseUrl);

//Configuramos los parametos que vaos a enviar con la peticion HTTP POST
                List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
                nvp.add(new BasicNameValuePair("article", jsonData));
//post.setHeader("Content-type", "application/json");
                post.setEntity(new UrlEncodedFormEntity(nvp));
//Se ejecuta el envio de la peticion y se espera la respuesta de la misma.
                HttpResponse response = httpClient.execute(post);
                Log.w("", response.getStatusLine().toString());

//Obtengo el contenido de la respuesta en formato InputStream Buffer y la paso a formato String
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                return "Exception happened: " + e.getMessage();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        protected void onProgressUpdate(Integer... progress) {
//Se obtiene el progreso de la peticion
            Log.w("", "Indicador de pregreso " + progress[0].toString());
        }

        protected void onPostExecute(String result) {
//Se obtiene el resultado de la peticion Asincrona
            Log.w("","Resultado obtenido " + result);
            processResult(result);
        }
    }

}
