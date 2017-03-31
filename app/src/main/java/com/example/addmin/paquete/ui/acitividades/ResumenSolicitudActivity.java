package com.example.addmin.paquete.ui.acitividades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.addmin.paquete.R;
import com.example.addmin.paquete.tools.Constantes;
import com.example.addmin.paquete.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResumenSolicitudActivity extends AppCompatActivity {

    String id_solicitud,total,carrier;
    TextView origen_detalle,destino_detalle,fecha_promesa,hora_promesa,tipo_recol_detall,n_paquetes,costo_aprox,operador_log;
    JSONArray origen,destino,datos;
    public static final String TAG = ResumenSolicitudActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_solicitud);

        origen_detalle = (TextView)findViewById(R.id.origen_detalle);
        destino_detalle = (TextView)findViewById(R.id.destino_detalle);
        fecha_promesa = (TextView)findViewById(R.id.fecha_promesa);
        hora_promesa = (TextView)findViewById(R.id.hora_promesa);
        tipo_recol_detall = (TextView)findViewById(R.id.tipo_recol_detall);
        n_paquetes = (TextView)findViewById(R.id.n_paquetes);
        costo_aprox = (TextView)findViewById(R.id.costo_aproximado);
        operador_log = (TextView)findViewById(R.id.operador_logistico);

        Bundle bundle = getIntent().getExtras();
        id_solicitud =bundle.getString("id_solicitud");
        total =bundle.getString("total");
        carrier =bundle.getString("carrier");

        cargarAdaptador();

    }

    public void cargarAdaptador() {
        VolleySingleton.
                getInstance(ResumenSolicitudActivity.this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                Constantes.SELECT_DATOS_SOLICITUD +"?id_solicitud="+id_solicitud,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuesta(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley : " + error.toString());
                                    }
                                }

                        )
                );
    }

    private void procesarRespuesta(JSONObject response) {
        try {
            String estado = response.getString("estado");
            switch (estado) {
                case "1":
                    origen = response.getJSONArray("datos_origen");
                    destino = response.getJSONArray("datos_destino");
                    datos = response.getJSONArray("datos_general");
                    adaptarOrigen(origen);
                    adaptarDestino(destino);
                    adaptarDatos(datos);
                    break;
                case "2":

                    break;
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }
    private void adaptarOrigen(JSONArray origen) throws JSONException {
        origen_detalle.setText("Calle "+
                origen.getJSONObject(0).getString("calleRecol")+"  #"+
                origen.getJSONObject(0).getString("numExtRecol")+" por "+
                origen.getJSONObject(0).getString("cruzRecol1")+" y "+
                origen.getJSONObject(0).getString("cruzRecol2")+" "+
                origen.getJSONObject(0).getString("colonia")+" "+
                origen.getJSONObject(0).getString("ciudad")+" "+
                origen.getJSONObject(0).getString("estado")
        );
    }

    private void adaptarDestino(JSONArray destino) throws JSONException {
        destino_detalle.setText("Calle "+
                        destino.getJSONObject(0).getString("calleDestino")+"  #"+
                        destino.getJSONObject(0).getString("numExtDestino")+" por "+
                        destino.getJSONObject(0).getString("cruzDestino1")+" y "+
                        destino.getJSONObject(0).getString("cruzDestino2")+" "+
                        destino.getJSONObject(0).getString("colonia")+" "+
                        destino.getJSONObject(0).getString("ciudad")+" "+
                        destino.getJSONObject(0).getString("estado"));
    }
    private void adaptarDatos(JSONArray datos) throws JSONException {
        fecha_promesa.setText(datos.getJSONObject(0).getString("fechaPromesa"));
        hora_promesa.setText(datos.getJSONObject(0).getString("horaPromesa"));
        tipo_recol_detall.setText(datos.getJSONObject(0).getString("recoleccion"));
        n_paquetes.setText(datos.getJSONObject(0).getString("NumPaquetes"));
        costo_aprox.setText(total);
        operador_log.setText(carrier);
    }

    public void salir(View view)
    {  android.os.Process.killProcess(android.os.Process.myPid());
        /*
        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }
}
