package com.example.carcontroll;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleyRequest {
    private final Context context;
    private final String urlBase;

    private final RequestQueue mRequestQueue;

    public String returnData;

    public VolleyRequest(Context _context, String _url /*,String _address*/) {
        context = _context;
        urlBase = _url;
        mRequestQueue = Volley.newRequestQueue(_context);
    }

    private void setReturn(String _response){
        returnData = _response;
    }
    private String getReturn(){
        if(returnData == null){
            returnData = "NO DATA/NO CONNECTION TO SERVER.";
        }
        return returnData;
    }

    public String SendRequest(@Nullable String... _request){
        String url;
        if(_request == null || _request.length == 0){
            url = urlBase + "defaults?"
                    .concat("drl=" + MainActivity.defDrlState.toLowerCase())
                    .concat("&int=" + MainActivity.defInterState.toLowerCase())
                    .concat("&amp=" + MainActivity.defAmpState.toLowerCase())
                    .concat("&dvr=" + MainActivity.defDvrState.toLowerCase())
                    .concat("&drlDelay=" + MainActivity.drlDelay.toLowerCase())
                    .concat("&intDelay=" + MainActivity.interDelay.toLowerCase())
                    .concat("&ampDelay=" + MainActivity.ampDelay.toLowerCase())
                    .concat("&dvrDelay=" + MainActivity.dvrDelay.toLowerCase());
        }else{
            url = urlBase + _request[0]; // build request here
            Log.w("WARNING",urlBase + _request[0]);
        }

        //String Request initialized
        StringRequest mStringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(context, "Response :" + response.toString(), Toast.LENGTH_LONG).show();
                        setReturn(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("INFO", "Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
        return getReturn();
    }

}
