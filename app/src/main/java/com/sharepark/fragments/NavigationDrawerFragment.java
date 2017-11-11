package com.sharepark.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sharepark.R;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private static NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private static DrawerLayout mDrawerLayout;
    private static ListView listView;
    private static View mFragmentContainerView;

    private static int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private Display display;
    private int stageWidth;
    RoundedImageView profilePic;
    String image;

    LinearLayout GoToProfile;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    String fb_pic_url;
    private TextView nameholder;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);


        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(getActivity(), mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View mDrawer = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        preferences = getActivity().getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();
        String name;


        nameholder = (TextView) mDrawer.findViewById(R.id.name_holder);
        nameholder.setText(preferences.getString("fname", "Name").toUpperCase()+" "+preferences.getString("lname", "Name").toUpperCase());

        TextView emailholder = (TextView) mDrawer.findViewById(R.id.email_holder);
        emailholder.setText(preferences.getString("email", null));

        Log.e("FB_ID", preferences.contains("fb_id")+"");


        profilePic = (RoundedImageView) mDrawer.findViewById(R.id.profilePic);
        Boolean isFacebook = preferences.getBoolean("facebook_login", false);
        String imageString = preferences.getString("prof_pic", "null");
        String fb_pic = preferences.getString("fb_pic", "null");
        Log.d("FB", isFacebook.toString());
        Log.d("FB PIC", imageString);

        Glide.with(getContext())
                .load(imageString)
                .placeholder(R.drawable.default_profile_image)
                .into(profilePic);

        display = getActivity().getWindowManager().getDefaultDisplay();
        stageWidth = display.getWidth();
        stageWidth = (int) (stageWidth * .85);

        GoToProfile = (LinearLayout) mDrawer.findViewById(R.id.gotoprofile);
        GoToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), ProfileActivity.class);
                //startActivity(intent);
         //       overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        mDrawer.post(new Runnable() {
            @Override
            public void run() {

                Resources resources = getResources();
                //float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, resources.getDisplayMetrics());
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
                params.width = (stageWidth);
                mDrawer.setLayoutParams(params);
            }


        });


        listView = (ListView) mDrawer.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // listView.setItemChecked(position, true);
                selectItem(getActivity(), position);
            }
        });

        DrawerListAdapter adapter = new DrawerListAdapter(getActivity(), generateData(getActivity(), mCurrentSelectedPosition));
        listView.setAdapter(adapter);
        return mDrawer;
    }


    private static ArrayList<DrawerListModel> generateData(Activity act, int indexChecked) {
        ArrayList<DrawerListModel> models = new ArrayList<>();
        models.add(new DrawerListModel(R.drawable.ic_maps, act.getString(R.string.title_section1), false));


        if (indexChecked > -1) {
            models.get(indexChecked).setStatus(true);
        }


        return models;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        /*ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);*/

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_menu,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public static void callClick(int position) {
        listView.performItemClick(
                listView.getAdapter().getView(position, null, null),
                position,
                listView.getAdapter().getItemId(position));
    }

    public static void selectItem(Activity mAct, int position) {
        mCurrentSelectedPosition = position;
        if (listView != null) {
            //listView.setItemChecked(position, true);
            DrawerListAdapter adapter = new DrawerListAdapter(mAct, generateData(mAct, mCurrentSelectedPosition));
            listView.setAdapter(adapter);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    public static void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    public static void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (NavigationDrawerCallbacks)context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
       // actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public static class DrawerListModel {

        private int icon;
        private String title;
        private boolean checked;


        public DrawerListModel() {
        }

        public DrawerListModel(int icon, String title, boolean checked) {
            super();
            this.icon = icon;
            this.title = title;
            this.checked = checked;
        }

        public void setStatus(boolean checked) {
            this.checked = checked;
        }

        public boolean getStatus() {
            return checked;
        }

        public String getTitle() {
            return title;
        }

        public int getIcon() {
            return icon;
        }

    }

    public static class DrawerListAdapter extends ArrayAdapter<DrawerListModel> {

        private final Context context;
        private final ArrayList<DrawerListModel> modelsArrayList;

        public DrawerListAdapter(Context context, ArrayList<DrawerListModel> modelsArrayList) {
            super(context, R.layout.item_drawer, modelsArrayList);

            this.context = context;
            this.modelsArrayList = modelsArrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 1. Create inflater
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 2. Get rowView from inflater

            View rowView = null;
            rowView = inflater.inflate(R.layout.item_drawer, parent, false);

            // 3. Get icon,title & counter views from the rowView
            LinearLayout drawerBackground = (LinearLayout) rowView.findViewById(R.id.drawerBackground);
            ImageView imgView = (ImageView) rowView.findViewById(R.id.drawerListImage);
            TextView titleView = (TextView) rowView.findViewById(R.id.drawerListText);

            if (modelsArrayList.get(position).getStatus())
                drawerBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.drawerAccent));
            else
                drawerBackground.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

            // 4. Set the text for textView
            imgView.setImageResource(modelsArrayList.get(position).getIcon());
            titleView.setText(modelsArrayList.get(position).getTitle());


            // 5. retrn rowView
            return rowView;
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        String imageString = preferences.getString("prof_pic", null);
        nameholder.setText(preferences.getString("fname", "Name").toUpperCase()+" "+preferences.getString("lname", "Name").toUpperCase());
        Glide.with(this)
                .load(imageString)
                .asBitmap()
                .placeholder(R.drawable.default_profile_image)
                .into(profilePic);


    }
}



