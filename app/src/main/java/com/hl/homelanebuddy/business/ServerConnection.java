package com.hl.homelanebuddy.business;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hl0204 on 11/1/16.
 *
 *
 *
 */
public final class ServerConnection {


    /**
     * Holds the reference to the Context Object
     */
    private final Context mContext;


    /**
     * Holds the reference to the request queue
     */
    private final RequestQueue mRequestQueue;

    /**
     * Constructs a new instance of {@code Object}.
     *
     * @param mContext the context in which the service is running
     */
    public ServerConnection(Context mContext){
        this.mContext = mContext;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mRequestMap = new HashMap<>();
    }


    /**
     * function which will create a get request and ass it to the queue
     *
     * @param url the end point from where the info to be fetched
     * @param urlParams the URL params to be appended
     * @param requestType the type of the request
     * @param listener the connection listener
     * @return the tag for the request this can be latter used to cancel a specific request
     */
    public String doGet(final String url, final Bundle urlParams, final int requestType,
                        final ServerConnectionListener listener){
        final String completeURL = url + appendUrlParams(urlParams, url.indexOf("?") != -1);
        final CustomHeaderRequest request = new CustomHeaderRequest(completeURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final ServerResponse responseObject = new ServerResponse();
                        responseObject.requestType = requestType;
                        responseObject.response = response;
                        listener.onFinish(responseObject);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        final ServerResponse response = new ServerResponse();
                        response.requestType = requestType;
                        response.error = error;
                        listener.onFinish(response);
                    }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(completeURL);
        mRequestQueue.add(request);
        return completeURL;
    }


    Map<String, CustomHeaderRequest> mRequestMap;

    /**
     * function create a post request object and add it to the queue
     *
     * @param url the end point from where the info to be fetched
     * @param urlParams the URL params to be appended
     * @param postBody the body to be send in the post request
     * @param requestType the type of the request
     * @param listener the connection listener
     * @return the tag for the request this can be latter used to cancel a specific request
     */
    public String doPost(final String url, final Bundle urlParams, final String postBody, final int requestType,
                         final ServerConnectionListener listener){
        final String completeURL = url + appendUrlParams(urlParams, url.indexOf("?") != -1);
        final CustomHeaderRequest request;
        request = new CustomHeaderRequest(Request.Method.POST, completeURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final ServerResponse responseObject = new ServerResponse();
                        responseObject.requestType = requestType;
                        responseObject.response = response;
                        responseObject.responseHeaders = mRequestMap.remove(completeURL).getmResponseHeaders();
                        listener.onFinish(responseObject);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mRequestMap.remove(completeURL);
                        final ServerResponse response = new ServerResponse();
                        response.requestType = requestType;
                        response.error = error;
                        listener.onFinish(response);
                    }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setBody(postBody);
        request.setTag(completeURL);
        mRequestMap.put(completeURL, request);
        mRequestQueue.add(request);
        return completeURL;
    }


    /**
     * function append the url params to the url if there any
     *
     * @param params the bundle containing the params
     * @param defaultParamsPresent denotes if whether the url has
     *                             default params or not
     * @return the url query params
     */
    private String appendUrlParams(final Bundle params, final boolean defaultParamsPresent){
        String urlParams = "?";
        if(params == null || params.isEmpty())
            return urlParams;
        for(final String key : params.keySet()){
            urlParams += "&" + key + "=" + params.getString(key);
        }
        return urlParams;
    }

    /**
     * function which cancel all the request in queue and
     * nullify the resources in use
     */
    public void shutDown(){
        mRequestQueue.stop();
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }


    public interface ServerConnectionListener{
        /**
         * function which will be called on when the request succeed
         *
         * @param data the data which will be send across
         */
        public void onFinish(ServerResponse data);
    }


    /**
     * Class holds the details about the request
     */
    public class ServerResponse{
        public VolleyError error;
        public String response;
        public int requestType;
        public Map<String, String> responseHeaders;
    }



}
