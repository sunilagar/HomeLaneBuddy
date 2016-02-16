/*
 * Copyright (c) 2015 HomeLane.com
 *  All Rights Reserved.
 *  All information contained herein is, and remains the property of HomeLane.com.
 *  The intellectual and technical concepts contained herein are proprietary to
 *  HomeLane.com Inc and may be covered by U.S. and Foreign Patents, patents in process,
 *  and are protected by trade secret or copyright law. This product can not be
 *  redistributed in full or parts without permission from HomeLane.com. Dissemination
 *  of this information or reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from HomeLane.com.
 * <p/>
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package com.hl.homelanebuddy.main.task;

import android.os.AsyncTask;
import android.os.Build;

import com.hl.hlcorelib.HLCoreLib;
import com.hl.hlcorelib.orm.HLObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by hl0204 on 7/9/15.
 */
public final class CommonJSONParser extends AsyncTask<String, Void, String> {


    private ParserCallback mCallback;




    /**
     * function initiate the JSON file load
     *
     * @param uri of type String the location of the JSON file in the files
     *
     */
    public void loadJSON(final String uri, ParserCallback mCallback){
        this.mCallback = mCallback;
//        this.mParser = parser;
        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{uri});
        }else{
            execute(new String[]{uri});
        }
    }


    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param list The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(String list) {
        super.onPostExecute(list);
        if(mCallback != null){
            mCallback.onParse(list);
            mCallback = null;
        }
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
    protected String doInBackground(String... params) {
        ArrayList<HLObject> result = null;
        BufferedReader fileReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStreamReader stream = new InputStreamReader(HLCoreLib.getAppContext().getResources().
                    getAssets().open(params[0]));
            fileReader = new BufferedReader(stream);
            String line = "";
            while ((line = fileReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonString = stringBuilder.toString();
            return jsonString;
        }catch (IOException e){
            e.printStackTrace();
//            QCLogger.log(e);
//            Crashlytics.getInstance().core.logException(e.getCause());
        }catch (Exception e){
            e.printStackTrace();
//            QCLogger.log(e);
//            Crashlytics.getInstance().core.logException(e.getCause());
        }
        return null;
    }

    /**
     * Callback to listen for the parsing
     */
    public static interface ParserCallback{
        /**
         * function will be called on parsing of the json
         *
         * @param list the list of object values
         */
        public void onParse(final String list);
    }
}
