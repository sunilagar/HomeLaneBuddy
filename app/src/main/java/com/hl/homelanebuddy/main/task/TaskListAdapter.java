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

package com.hl.homelanebuddy.main.task;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.hl.hlcorelib.mvp.events.HLCoreEvent;
import com.hl.hlcorelib.mvp.events.HLEventDispatcher;
import com.hl.hlcorelib.orm.HLObject;
import com.hl.homelanebuddy.Constants;
import com.hl.homelanebuddy.R;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hl0204 on 29/7/15.
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    /**
     * setter function for mDataSet
     *
     * @param mDataSet data set to be saved
     */
    public void setmDataSet(ArrayList<HLObject> mDataSet) {
        this.mDataSet = mDataSet;
        notifyDataSetChanged();
    }

    /**
     * getter function for mDataSet
     * @return the ArrayList containing the values
     */
    public ArrayList<HLObject> getmDataSet() {
        return mDataSet;
    }

    private ArrayList<HLObject> mDataSet;

    /**
     * ViewHolder class loads the views for the Recyler view item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mTaskName;
        public TextView mTaskDate;
        public TextView mTaskDay;
        public ImageView mTaskStatus;
        public TextView mTaskReview;
        public View mStatusColor;
        public CardView mCardView;

        /**
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            mTaskName = (TextView)itemView.findViewById(R.id.task_name);
            mTaskDate = (TextView)itemView.findViewById(R.id.date_text);
            mTaskDay = (TextView)itemView.findViewById(R.id.day_text);
            mTaskReview = (TextView)itemView.findViewById(R.id.rating_text);
            mTaskStatus    = (ImageView)itemView.findViewById(R.id.task_status);
//            mChat = (ImageView) itemView.findViewById(R.id.chat_view);
            mStatusColor = (View)itemView.findViewById(R.id.status_color);
            mCardView = (CardView)itemView.findViewById(R.id.card_view);
        }
    }

    /**
     * Constructor function set the dataset and return the instance
     * @param mList the data provider for the list
     */
    public TaskListAdapter(ArrayList<HLObject> mList) {
        super();
        this.mDataSet = mList;
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return ((mDataSet != null) ? mDataSet.size() : 0);
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_item1, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
     * the given position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this
     * method again if the position of the item changes in the data set unless the item itself
     * is invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside this
     * method and should not keep a copy of it. If you need the position of an item later on
     * (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will have
     * the updated adapter position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HLObject task = (HLObject)mDataSet.get(position);
        holder.mTaskName.setText(task.getString(Constants.Task.TASK_NAME));
        if(task.getString(Constants.Task.TASK_DATE)!=null &&
                task.getString(Constants.Task.TASK_DATE).length()>0) {
            holder.mTaskDate.setText(convertTime(Long.parseLong(task.getString(Constants.Task.TASK_DATE)),"dd"));
            holder.mTaskDay.setText(convertTime(Long.parseLong(task.getString(Constants.Task.TASK_DATE)), "MMM"));
            if (System.currentTimeMillis() >= Long.parseLong(task.getString(Constants.Task.TASK_DATE))) {
                holder.mTaskReview.setEnabled(true);
                holder.mCardView.setCardBackgroundColor(holder.mTaskReview.getContext().getResources().
                        getColor(R.color.card_view_green));
                holder.mTaskReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.Task.TASK_NAME, task.getString(Constants.Task.TASK_NAME));
                        bundle.putParcelableArrayList(Constants.Task.NAME, mDataSet);

                        HLCoreEvent event = new HLCoreEvent(Constants.USER_REVIEW_EVENT,
                                bundle);
                        HLEventDispatcher.acquire().dispatchEvent(event);

                    }
                });

            } else {
                holder.mTaskReview.setEnabled(false);
                holder.mCardView.setCardBackgroundColor(holder.mTaskReview.getContext().getResources().
                        getColor(R.color.card_view_gray));
            }


            holder.mTaskStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(holder.mTaskStatus.getContext(), v);

                    try {
                        Field[] fields = popupMenu.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            if ("mPopup".equals(field.getName())) {
                                field.setAccessible(true);
                                Object menuPopupHelper = field.get(popupMenu);
                                Class<?> classPopupHelper = Class.forName(menuPopupHelper
                                        .getClass().getName());
                                Method setForceIcons = classPopupHelper.getMethod(
                                        "setForceShowIcon", boolean.class);
                                setForceIcons.invoke(menuPopupHelper, true);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.getInstance().core.logException(e.getCause());
                    }

                    popupMenu.getMenuInflater().inflate(R.menu.task_status, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

        }
    }



    public String convertTime(long time, String f){
        Date date = new Date(time);
        Format format = new SimpleDateFormat(f);
        return format.format(date);
    }

}
