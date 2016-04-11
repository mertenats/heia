package ch.heia.mobiledev.navigation;

/*
    TP03 NavigationHomeActivity
    Gremaud D., Mertenat S.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NavigationHomeActivity extends ListActivity {
    private static final String TAG = NavigationHomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Log.d(TAG, "NavigationHomeActivity.onCreate called");

        // step 5
        //SampleAdapter l_sampleAdapter = new SampleAdapter();
        //setListAdapter(l_sampleAdapter);


        // step 10
        ActivityListAdapter l_activityListAdapter = new ActivityListAdapter();
        setListAdapter(l_activityListAdapter);
    }

    // Override lifecycle callback methods (step 2)
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "NavigationHomeActivity.onStart() called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "NavigationHomeActivity.onRestart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "NavigationHomeActivity.onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "NavigationHomeActivity.onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "NavigationHomeActivity.onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "NavigationHomeActivity.onDestroy() called");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "NavigationHomeActivity.onNewIntent() called");
    }

   /* // step 7
    // this method is called automatically after setListAdapter()
    // then it calls the getView() method
    protected void onListItemClick(ListView p_listView, View p_view, int p_position, long p_id) {
        Log.d(TAG, "NavigationHomeActivity.onListItemClick() called");
        //Log.d(TAG, "onListItemClick() : " + p_listView.getAdapter().getItem(p_position)); // string name
        //Log.d(TAG, "onListItemClick() : " + p_listView.getAdapter().getCount()); // list count

        // get the TextView reference
        // change the text color to red
        TextView l_textView = (TextView) getListView().getChildAt(p_position);
        l_textView.setTextColor(Color.RED);
    }*/

    // step 10
    // this method is called automatically after setListAdapter()
    // then it calls the getView() method
    protected void onListItemClick(ListView p_listView, View p_view, int p_position, long p_id) {
        Log.d(TAG, "NavigationHomeActivity.onListItemClick() called");

        ActivityInfo l_ai = (ActivityInfo)getListAdapter().getItem(p_position);
        // call the selected activity
        startActivity(l_ai.m_intent);
    }

    // step 9
    private ArrayList<ActivityInfo> listActivities() {
        // create a new intent
        Intent l_intent = new Intent();
        l_intent.setPackage(getPackageName());
        l_intent.setAction(Intent.ACTION_MAIN);
        l_intent.addCategory(Intent.CATEGORY_DEFAULT);

        List<ResolveInfo> l_resolveInfoList = getPackageManager().queryIntentActivities(l_intent, PackageManager.MATCH_ALL);
        ArrayList<ActivityInfo> l_activityInfoList = new ArrayList<>();

        String l_package_name = getPackageName() + "."; // used to retrieve the class name for the activity

        for (ResolveInfo item : l_resolveInfoList) {
            // create a new intent
            Intent l_activityInfoIntent = new Intent();
            l_activityInfoIntent.setClassName(NavigationHomeActivity.this, item.activityInfo.name);

            // Create an ActivityInfo instance + remove the package name
            ActivityInfo l_ai = new ActivityInfo(item.activityInfo.name.replace(l_package_name, ""), l_activityInfoIntent);
            l_activityInfoList.add(l_ai);
        }
        return l_activityInfoList;
    }

    // step 4 - 6
    public class SampleAdapter extends BaseAdapter {
        private final ArrayList<String> m_stringsList;

        /**
         * Constructor
         */
        public SampleAdapter() {
            ArrayList<String> l_stringsList = new ArrayList<>();
            // add 3 strings to the ArrayList
            l_stringsList.add("String 1");
            l_stringsList.add("String 2");
            l_stringsList.add("String 3");
            this.m_stringsList = l_stringsList;
        }

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return this.m_stringsList.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param p_position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int p_position) {
            return this.m_stringsList.get(p_position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param p_position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int p_position) {
            //return this.m_stringsList.indexOf(p_position);
            return p_position;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param p_position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param p_convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param p_parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int p_position, View p_convertView, ViewGroup p_parent) {
            // check if the old view is non-null and of an appropriate type
            if (p_convertView == null || !(p_convertView instanceof TextView)) {
                // check if the position matches the number of entries
                if (p_position < this.getCount()) {
                    // create a new instance
                    TextView l_textView = new TextView(getApplicationContext());
                    l_textView.setText(this.m_stringsList.get(p_position));
                    l_textView.setTextColor(Color.BLACK);
                    l_textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    return l_textView;
                } else {
                    return null;
                }
            } else {
                // or re-use the old one
                if (p_position < this.getCount()) {
                    ((TextView) p_convertView).setText(this.m_stringsList.get(p_position));
                    ((TextView) p_convertView).setTextColor(Color.BLACK);
                    ((TextView) p_convertView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    return p_convertView;
                } else {
                    return null;
                }
            }
        }

    }


    // step 8
    public class ActivityInfo {
        // class members
        private final String m_name;
        private final Intent m_intent;

        public ActivityInfo(String p_name, Intent p_intent) {
            this.m_name = p_name;
            this.m_intent = p_intent;
        }
    }


    // step 10
    public class ActivityListAdapter extends BaseAdapter {
        private final ArrayList<ActivityInfo> m_activityInfoList;

        public ActivityListAdapter() {
            this.m_activityInfoList = listActivities();
        }

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return this.m_activityInfoList.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param p_position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int p_position) {
            return this.m_activityInfoList.get(p_position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param p_position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int p_position) {
            //return this.m_activityInfoList.indexOf(p_position);
            return p_position;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param p_position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param p_convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param p_parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int p_position, View p_convertView, ViewGroup p_parent) {
            // check if the old view is non-null and of an appropriate type
            if (p_convertView == null || !(p_convertView instanceof TextView)) {
                // check if the position matches the number of entries
                if (p_position < this.getCount()) {
                    // create a new instance
                    TextView l_textView = new TextView(getApplicationContext());
                    l_textView.setText(this.m_activityInfoList.get(p_position).m_name);
                    l_textView.setTextColor(Color.BLACK);
                    l_textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    return l_textView;
                } else {
                    return null;
                }
            } else {
                // or re-use the old-one
                if (p_position < this.getCount()) {
                    ((TextView) p_convertView).setText(this.m_activityInfoList.get(p_position).m_name);
                    ((TextView) p_convertView).setTextColor(Color.BLACK);
                    ((TextView) p_convertView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    return p_convertView;
                } else {
                    return null;
                }
            }
        }

    }

}
