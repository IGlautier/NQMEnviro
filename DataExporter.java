package com.nqm.nqmenviro;

/**
 * Created by Ivan on 10/08/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ivan on 05/08/2016.
 */
public class DataExporter {
    private String login;
    private String accessToken;
    private double tokenBirth;
    private Context context;
    private RequestQueue queue;
    private String asset;
    private Boolean create;

    public DataExporter(String token, Context _context, String _asset, Boolean _create) {
        this.login = token;
        this.context = _context;
        this.asset = _asset;
        this.create = _create;
        queue = Volley.newRequestQueue(context);
    }

    public void getAccessToken() {
        String url ="https://cmd.nqminds.com/token";

        // Request a string response from the provided URL.
        JSONObject params = new JSONObject();
        try {
            params.put("grant_type", "client_credentials");
            params.put("ttl", 3600);
        } catch (JSONException err) {
            err.printStackTrace();
        }


        final Map<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("authorization", "Basic " + login);

        JsonObjectRequest rq = new JsonObjectRequest(url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            accessToken = response.getString("access_token");
                            tokenBirth = System.currentTimeMillis()/1000;
                            Toast.makeText(context, "Valid Credentials", Toast.LENGTH_SHORT).show();
                            if (create) createDataSet(asset);
                        } catch (JSONException err) {
                            err.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(rq);
    }

    public void createDataSet(String setName) {
        String url = "https://cmd.nqminds.com/commandSync/resource/create";

        JSONObject params = new JSONObject();
        try {
            params.put("parentId", "B1g7JQ1HP");
            params.put("name", setName);
            params.put("basedOnSchema", "AndroidEnvironmentData");
        } catch (JSONException err) {
            err.printStackTrace();
        }

        final Map<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("authorization", "Bearer " + accessToken);
        mHeaders.put("Content-Type", "application/json");

        JsonObjectRequest rq = new JsonObjectRequest(url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject res = response.getJSONObject("response");
                            asset = res.getString("id");
                            SharedPreferences prefs = context.getSharedPreferences("UserData", 0);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("assetName", asset);
                            editor.commit();
                            Toast.makeText(context, "Resource created", Toast.LENGTH_SHORT).show();
                        } catch (JSONException err) {
                            err.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Failed to create resource", Toast.LENGTH_SHORT).show();
            }
        }
        )
        {
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        rq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(rq);

    }

    public void exportData(JSONArray data) {

        if ((System.currentTimeMillis()/1000 - tokenBirth) > 3600) {
            getAccessToken();
        }

        String url ="https://cmd.nqminds.com/commandSync/dataset/data/createMany";

        // Request a string response from the provided URL.
        JSONObject params = new JSONObject();
        try {
            params.put("datasetId", asset);
            params.put("payload", data);
        } catch (JSONException err) {
            err.printStackTrace();
        }

        final Map<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("authorization", "Bearer " + accessToken);
        mHeaders.put("Content-Type", "application/json");

        JsonObjectRequest rq = new JsonObjectRequest(url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        )
        {
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };

        rq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 5, 2.0f));

        // Add the request to the RequestQueue.
        queue.add(rq);


    }
}
