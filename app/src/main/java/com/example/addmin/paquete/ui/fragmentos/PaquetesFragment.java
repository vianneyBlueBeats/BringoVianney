package com.example.addmin.paquete.ui.fragmentos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.addmin.paquete.ui.adaptadores.PaqueteAdapter;
import com.example.addmin.paquete.R;
import com.example.addmin.paquete.modelo.Paquete;
import com.example.addmin.paquete.tools.Constantes;
import com.example.addmin.paquete.ui.acitividades.PaqueteActivity;
import com.example.addmin.paquete.ui.acitividades.SobreActivity;
import com.example.addmin.paquete.ui.acitividades.TiporecolActivity;
import com.example.addmin.paquete.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class PaquetesFragment extends Fragment {
    private static final String TAG = PaquetesFragment.class.getSimpleName();
    private PaqueteAdapter adapter;
    private RecyclerView lista;
    private RecyclerView.LayoutManager lManager;
    Button agregar_paq,continuar_paq;
    com.melnykov.fab.FloatingActionButton fab;
    private Gson gson = new Gson();
    String id_solicitud ;
    ViewGroup.LayoutParams hh;

    public PaquetesFragment() {
    }

    public static PaquetesFragment createInstance(String id_solcitud) {
        PaquetesFragment paquetesFragment = new PaquetesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id_solicitud", id_solcitud);
        paquetesFragment.setArguments(bundle);
        return paquetesFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_paquetes, container, false);
        lista = (RecyclerView) v.findViewById(R.id.reciclador_paquete);
        lista.setHasFixedSize(true);
        lManager = new LinearLayoutManager(getActivity());
        lista.setLayoutManager(lManager);
        id_solicitud = getArguments().getString("id_solicitud");

        cargarAdaptador();

        continuar_paq = (Button)v.findViewById(R.id.continuar_paq);
        continuar_paq.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), TiporecolActivity.class);
                        i.putExtra("id_solicitud",id_solicitud);
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
                    }
                }
        );

        agregar_paq = (Button)v.findViewById(R.id.agregar_btn);
        agregar_paq.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog levelDialog = null;
                        final CharSequence[] items = {"Paquete"," Sobre"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Select tipo de producto");
                        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch(item)
                                {
                                    case 0:
                                        Intent i = new Intent(getActivity(), PaqueteActivity.class);
                                        i.putExtra("id_solicitud",id_solicitud);
                                        startActivityForResult(i, 3);
                                        break;
                                    case 1:
                                        i = new Intent(getActivity(), SobreActivity.class);
                                        i.putExtra("id_solicitud", id_solicitud);
                                        startActivityForResult(i,3);
                                        break;
                                }
                            }
                        });
                        levelDialog = builder.create();
                        levelDialog.show();
                    }
                }
        );

        return v;
    }

    public void cargarAdaptador() {
        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                Constantes.SELECT_PAQUETES +"?id_solicitud="+id_solicitud,
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
                    JSONArray mensaje = response.getJSONArray("paquetes");
                    Paquete[] paquetes = gson.fromJson(mensaje.toString(), Paquete[].class);
                    adapter = new PaqueteAdapter(Arrays.asList(paquetes), getActivity());
                    lista.setAdapter(adapter);
                    continuar_paq.setEnabled(true);
                    break;
                case "2":
                    hh  = lista.getLayoutParams();
                    hh.height = 600;
                    lista.setLayoutParams(hh);
                    continuar_paq.setEnabled(false);
                    break;
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }

}

