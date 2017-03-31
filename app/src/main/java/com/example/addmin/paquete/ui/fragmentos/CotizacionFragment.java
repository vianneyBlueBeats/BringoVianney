package com.example.addmin.paquete.ui.fragmentos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.addmin.paquete.R;
import com.example.addmin.paquete.ui.acitividades.ResumenSolicitudActivity;
import com.example.addmin.paquete.modelo.Cotizacion;
import com.example.addmin.paquete.tools.Constantes;
import com.example.addmin.paquete.ui.adaptadores.CotizacionAdapter;
import com.example.addmin.paquete.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class CotizacionFragment extends Fragment {
    private static final String TAG = CotizacionFragment.class.getSimpleName();
    private CotizacionAdapter adapter;
    private RecyclerView lista;
    private RecyclerView.LayoutManager lManager;
    String id_solicitud;
    JSONArray mensaje;
    Cotizacion[] cotizaciones;

    Button boton;
    private Gson gson = new Gson();

    public CotizacionFragment() {
    }

    public static CotizacionFragment createInstance(String id_solcitud) {
        CotizacionFragment cotizacionFragment = new CotizacionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id_solicitud", id_solcitud);
        cotizacionFragment.setArguments(bundle);
        return cotizacionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_cotizacion, container, false);

        lista = (RecyclerView) v.findViewById(R.id.reciclador_cotizacion);
        lista.setHasFixedSize(true);
        lManager = new LinearLayoutManager(getActivity());
        lista.setLayoutManager(lManager);
        id_solicitud = getArguments().getString("id_solicitud");
        cargarAdaptador();

        boton = (Button) v.findViewById(R.id.continuar_pago);
        boton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int var = adapter.mSelectedItem;
            if(var > -1)
            {
                String precio = adapter.precio;
                String embalaje = adapter.embalaje;
                String carrier = adapter.carrier;
                Intent i = new Intent(getActivity(),ResumenSolicitudActivity.class);
                i.putExtra("id_solicitud",id_solicitud);
                i.putExtra("total",precio);
                i.putExtra("carrier",carrier);
                startActivity(i);

            }
        }
        });
        return v;
    }

    public void cargarAdaptador() {
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                        new JsonObjectRequest(Request.Method.GET, Constantes.SELECT_COTIZACION, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuesta(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.toString());
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
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Cotizando...");
                    progressDialog.show();

                    mensaje = response.getJSONArray("cotizaciones");
                    cotizaciones = gson.fromJson(mensaje.toString(), Cotizacion[].class);

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    adapter = new CotizacionAdapter(Arrays.asList(cotizaciones), getActivity());
                                    lista.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                                    lista.setAdapter(adapter);
                                }
                            }, 3000);
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(getActivity(), mensaje2, Toast.LENGTH_LONG).show();
                    break;
            }
        }
        catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
    }

}

