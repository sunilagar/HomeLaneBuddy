package com.hl.homelanebuddy.main.task;

import android.content.Context;
import android.os.AsyncTask;

import com.hl.homelanebuddy.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by hl0204 on 3/2/16.
 */
public class TaskParser extends AsyncTask<Void, Void, String> {


    private ParseCallBack mCallback;

    private Context mContext;

    /**
     * function which will start fetching the local json
     *
     * @param callback to handle the parse result
     */
    public void fetchLocalJSON(final ParseCallBack callback, final Context mContext){
        this.mCallback = callback;
        this.mContext = mContext;
        execute(new Void[]{});
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected String doInBackground(Void... params) {
        InputStream is = mContext.getResources().openRawResource(R.raw.tasks);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            is.close();
        }catch (Exception e){

        }
        String jsonString = writer.toString();
        return jsonString;
    }


    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param s The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mCallback.deliver(s);
    }

    /**
     * Callback
     */
    public static interface ParseCallBack{
        /**
         * function which will deliver the parsed result
         * @param jsonString the list which is created
         */
        public void deliver(String jsonString);
    }
}
