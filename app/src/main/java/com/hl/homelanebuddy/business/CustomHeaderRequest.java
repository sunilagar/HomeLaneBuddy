package com.hl.homelanebuddy.business;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by hl0204 on 12/1/16.
 */
public class CustomHeaderRequest extends StringRequest {


    public Map<String, String> getmResponseHeaders() {
        return mResponseHeaders;
    }

    private Map<String, String> mResponseHeaders;

    /**
     * The body to be send in the post request
     */
    private String mBody;

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public CustomHeaderRequest(int method, String url, Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        if(response.headers != null){
            mResponseHeaders = response.headers;
        }
        return super.parseNetworkResponse(response);
    }

    /**
     * function get the header value against the key provided
     *
     * @param key the key against which the header value to be obtained
     * @return the value of header against the key
     */
    public String getResponseHeader(final String key){
        return mResponseHeaders.get(key);
    }

    /**
     * Creates a new GET request.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public CustomHeaderRequest(String url, Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can
     * throw {@link AuthFailureError} as authentication may be required to
     * provide these values.
     *
     * @throws AuthFailureError In the event of auth failure
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
//        final Map<String, String> headers = new HashMap<>(3);
//        headers.put("Accept", "application/json");
//        headers.put(Constants.APPConfig.LIVE_PERSON_AUTH,
//                HLCoreLib.readProperty(Constants.APPConfig.LIVE_PERSON_AUTH));
//        headers.put("Content-Type", "application/json");
//        return headers;
        return super.getHeaders();
    }

    /**
     * function which set the data for the post request body
     *
     * @param body the data to be send in post body
     */
    public void setBody(final String body){
        mBody = body;
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     * <p>
     * <p>By default, the body consists of the request parameters in
     * application/x-www-form-urlencoded format. When overriding this method, consider overriding
     * {@link #getBodyContentType()} as well to match the new body format.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    @Override
    public byte[] getBody() throws AuthFailureError {
        if(mBody == null)
            return super.getBody();
        return mBody.getBytes();
    }

    /**
     * Returns the content type of the POST or PUT body.
     */
    @Override
    public String getBodyContentType() {
        return "application/json";
    }
}
