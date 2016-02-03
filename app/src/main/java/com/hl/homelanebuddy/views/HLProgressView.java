/*
 * Copyright (c) 2015 HomeLane.com
 *  All Rights Reserved.
 *  All information contained herein is, and remains the property of HomeLane.com.
 *  The intellectual and technical concepts contained herein are proprietary to
 *  HomeLane.com Inc and may be covered by U.S. and Foreign Patents, patents in process,
 *  and are protected by trade secret or copyright law. This product can not be
 *  redistributed in full or parts without permission from HomeLane.com. Dissemination
 *  of this information or reproduction of this material is strictly forbidden unless
 *  prior written permission is obtained from HomeLane.com.
 *  <p/>
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package com.hl.homelanebuddy.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hl.homelanebuddy.R;


/**
 * Created by hl0204 on 28/7/15.
 */
public class HLProgressView extends RelativeLayout {

    /**
     * Holds the instance of ProgressBar
     */
    ProgressBar mProgress;

    private static final int ID = 1000;

    /**
     * Holds the instance of the TextView
     */
    TextView mErrorText;

    /**
     * Constructor funcltion
     * @param context under which the view will be created
     */
    public HLProgressView(Context context) {
        super(context);
    }


    /**
     *
     * @param context
     * @param attrs
     */
    public HLProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public HLProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }





    /**
     * Finalize inflating a view from XML.  This is called as the last phase
     * of inflation, after all child views have been added.
     * <p/>
     * <p>Even if the subclass overrides onFinishInflate, they should always be
     * sure to call the super method, so that we get called.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(mProgress == null){
            mProgress = new ProgressBar(getContext());
            mProgress.setId(getResources().getInteger(R.integer.db_version));
            mErrorText = new TextView(getContext());
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(CENTER_IN_PARENT);
            addView(mProgress, params);
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(CENTER_IN_PARENT);
            params.addRule(BELOW, mProgress.getId());
            addView(mErrorText, params);
        }
    }

    /**
     * Function shows the progress bar and hide the error text
     */
    public void showProgress(){
        mProgress.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.GONE);
    }

    /**
     * Function hides the progress bar and hide the error text
     */
    public void hideProgress(){
        mProgress.setVisibility(View.GONE);
        mErrorText.setVisibility(View.GONE);
    }

    /**
     * Function shows the error bar and hide the progress
     */
    public void showError(String error){
        mProgress.setVisibility(View.GONE);
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText(error);
    }
}
