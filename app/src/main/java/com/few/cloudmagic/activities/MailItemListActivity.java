package com.few.cloudmagic.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.few.cloudmagic.R;
import com.few.cloudmagic.fragments.CircularRevealingDetailFragment;
import com.few.cloudmagic.fragments.MailItemDetailFragment;
import com.few.cloudmagic.interfaces.OnFragmentTouched;
import com.few.cloudmagic.pojo.SingletonClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An activity representing a list of MailItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MailItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MailItemListActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,OnFragmentTouched, View.OnTouchListener {

        private static final Object DELTAG = "DELETE_TAG";
        /**
         * Whether or not the activity is in two-pane mode, i.e. running on a tablet
         * device.
         */
        private boolean mTwoPane;
        ArrayList<SingletonClass> listArray;
        private final static String TAG_FRAGMENT = "TAG_FRAGMENT";
        @Bind(R.id.drawer_layout)DrawerLayout drawer;
        @Bind(R.id.mailitem_list)RecyclerView recyclerView;
        @Bind(R.id.toolbar)Toolbar toolbar;
        public static final String TAG = "LISTAPI";
        RequestQueue queue,deleteQueue;
        ActionBarDrawerToggle mDrawerToggle;
        float x,y;
        boolean isMenuOn=false;
        int masterID,masterPosition;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mailitem_list);
            ButterKnife.bind(this);
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
            setupDrawer();
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            if (findViewById(R.id.mailitem_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;
            }

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            APICallList();
        }

        private void setupDrawer(){
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,  drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close
            );
            drawer.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        if(!mTwoPane) {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onBackPressed();
                                }
                            });
                        }
                        isMenuOn=true;
                        invalidateOptionsMenu();
                    } else {
                        //show hamburger
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        mDrawerToggle.syncState();
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drawer.openDrawer(GravityCompat.START);
                            }
                        });
                        isMenuOn=false;
                        invalidateOptionsMenu();
                    }
                }
            });
        }

        private void setupRecyclerView(@NonNull RecyclerView recyclerView,ArrayList<SingletonClass> list) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(list,this));
        }

        private void APICallList(){
            listArray=new ArrayList<>();
            String url=getString(R.string.listAPI);
            queue = Volley.newRequestQueue(this);
            JsonArrayRequest JSONARRRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Display the first 500 characters of the response string.
                            for(int count=0;count<response.length();count++){
                                try {
                                    JSONObject mailItem=new JSONObject(response.getString(count));
                                    SingletonClass pojo=new SingletonClass();
                                    pojo.setId(mailItem.getInt("id"));
                                    pojo.setTs(mailItem.getInt("ts"));
                                    pojo.setRead(mailItem.getBoolean("isRead"));
                                    pojo.setStarred(mailItem.getBoolean("isStarred"));
                                    pojo.setPreview(mailItem.getString("preview"));
                                    pojo.setSubject(mailItem.getString("subject"));
                                    pojo.setParticipants(mailItem.getJSONArray("participants"));
                                    listArray.add(pojo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            setupRecyclerView(recyclerView,listArray);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MailItemListActivity.this, "Oops! something went wrong. " +
                                    "Please try again",
                            Toast.LENGTH_SHORT).show();
                }
            });
            JSONARRRequest.setTag(TAG);
            JSONARRRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1));
            queue.add(JSONARRRequest);
        }



        @Override
        protected void onStop () {
            super.onStop();
            if (queue != null) {
                queue.cancelAll(TAG);
            }
        }

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            if (id == R.id.nav_inbox) {
                // Handle the camera action
            } else if (id == R.id.nav_sent) {

            } else if (id == R.id.nav_drafts) {

            } else if (id == R.id.nav_outbox) {

            } else if (id == R.id.nav_spam) {

            } else if (id == R.id.nav_trash) {

            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            if(isMenuOn) {
                getMenuInflater().inflate(R.menu.menu_delete, menu);
                return true;
            }else
                return false;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_delete) {
                callDeleteAPI();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void callDeleteAPI() {
            String url=getString(R.string.listAPI)+masterID;
            deleteQueue = Volley.newRequestQueue(this);
            StringRequest jsObjRequest = new StringRequest(Request.Method.DELETE, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            listArray.remove(masterPosition);
                            setupRecyclerView(recyclerView,listArray);
                            onBackPressed();
                            Toast.makeText(MailItemListActivity.this, "Message Deleted Successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error.
                            Toast.makeText(MailItemListActivity.this, "Oops! something went wrong. " +
                                    "Please try again",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
            );
            jsObjRequest.setTag(DELTAG);
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1));
            deleteQueue.add(jsObjRequest);
        }

        @Override
        public void onFragmentTouched(Fragment fragment, float x, float y) {

        }

        public void addFragment(View v,Bundle arguments) {
            int whiteColor = ContextCompat.getColor(this, R.color.white);
            Fragment fragment = CircularRevealingDetailFragment.newInstance((int)x,(int)y, whiteColor);
            fragment.setArguments(arguments);
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, fragment, TAG_FRAGMENT);
            transaction.addToBackStack(null);
            transaction.commit();

        }

    public void addFragmentTwoPane(View v,Bundle arguments) {
        int whiteColor = ContextCompat.getColor(this, R.color.white);
        Fragment fragment = CircularRevealingDetailFragment.newInstance((int)x,(int)y, whiteColor);
        fragment.setArguments(arguments);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mailitem_detail_container, fragment, TAG_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();

    }



        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            x = motionEvent.getX();
            y = motionEvent.getY();
            return true;
        }


        @Override
        public void onBackPressed() {
            try {
                final CircularRevealingDetailFragment fragment =
                        (CircularRevealingDetailFragment) getSupportFragmentManager().
                                findFragmentByTag(TAG_FRAGMENT);
                if (fragment.isVisible()) {
                    FragmentManager manager = getSupportFragmentManager();
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    manager.popBackStack();
                    mDrawerToggle.syncState();
                }
            }catch (Exception e){
                finish();
            }
        }



        public class SimpleItemRecyclerViewAdapter
                extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

            private final ArrayList<SingletonClass> mValues;
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int colorRead,colorNotRead;
            int tintBorder,tintFull;
            Context mContext;


            public SimpleItemRecyclerViewAdapter(ArrayList<SingletonClass> items,Context mContext) {
                mValues = items;
                colorRead= ContextCompat.getColor(mContext, R.color.DividerColor);
                colorNotRead=ContextCompat.getColor(mContext, R.color.white);
                tintBorder=ContextCompat.getColor(mContext,R.color.SecondaryText);
                tintFull=ContextCompat.getColor(mContext,R.color.amber);
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mailitem_list_content, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final ViewHolder holder, final int position) {
                try {
                    holder.mItem = mValues.get(position);
                    final int Randomcolor=generator.getRandomColor();
                    final JSONArray participants = mValues.get(position).getParticipants();
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(String.valueOf(participants.getString(0).charAt(0)),
                                    Randomcolor);
                    holder.Contact.setText(getParticipants(participants));
                    holder.Subject.setText(mValues.get(position).getSubject());
                    holder.Content.setText(mValues.get(position).getPreview());
                    holder.imageDrawable.setImageDrawable(drawable);
                    int color=(mValues.get(position).isRead())?colorRead:colorNotRead;
                    holder.background.setBackgroundColor(color);
                    final int image=(mValues.get(position).isStarred())?R.drawable.ic_star_black_24dp:
                            R.drawable.ic_star_border_black_24dp;
                    final int tint=(mValues.get(position).isStarred())?tintFull:tintBorder;
                    holder.starred.setImageResource(image);
                    holder.starred.setColorFilter(tint);
                    final String timeStamp=getDate(mValues.get(position).getTs());
                    holder.time.setText(timeStamp);
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mTwoPane) {
    //                            Bundle arguments = new Bundle();
    //                            arguments.putString(MailItemDetailFragment.ARG_ITEM_ID, holder.mItem.getSubject());
    //                            MailItemDetailFragment fragment = new MailItemDetailFragment();
    //                            fragment.setArguments(arguments);
    //                            getSupportFragmentManager().beginTransaction()
    //                                    .replace(R.id.mailitem_detail_container, fragment)
    //                                    .addToBackStack("mail")
    //                                    .commit();
                                masterPosition=position;
                                masterID=mValues.get(position).getId();
                                Bundle arguments=new Bundle();
                                arguments.putString("Contact",getParticipants(participants));
                                arguments.putString("Subject",mValues.get(position).getSubject());
                                arguments.putString("Content",mValues.get(position).getPreview());
                                arguments.putInt("color",Randomcolor);
                                arguments.putInt("startTint",tint);
                                arguments.putString("timeStamp",timeStamp);
                                arguments.putInt("star",image);
                                arguments.putInt("tint",tint);
                                arguments.putInt("ID",mValues.get(position).getId());
                                addFragmentTwoPane(v,arguments);

                            } else {
                                masterPosition=position;
                                masterID=mValues.get(position).getId();
                                Bundle arguments=new Bundle();
                                arguments.putString("Contact",getParticipants(participants));
                                arguments.putString("Subject",mValues.get(position).getSubject());
                                arguments.putString("Content",mValues.get(position).getPreview());
                                arguments.putInt("color",Randomcolor);
                                arguments.putInt("startTint",tint);
                                arguments.putString("timeStamp",timeStamp);
                                arguments.putInt("star",image);
                                arguments.putInt("tint",tint);
                                arguments.putInt("ID",mValues.get(position).getId());
                                addFragment(v,arguments);
                            }
                        }
                    });
                }catch (Exception ignored){}
            }


            private String getParticipants(JSONArray participants) {
                String consoliated="";
                for(int count=0;count<participants.length();count++){
                    try {
                        consoliated+=participants.getString(count)+", ";

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return method(consoliated.trim());
            }

            private String getDate(int timeStampVal){
                long timeStamp = timeStampVal * 1000;
                try{
                    DateFormat sdf = new SimpleDateFormat("MMM dd");
                    Date netDate = (new Date(timeStamp));
                    return sdf.format(netDate);
                }
                catch(Exception ex){
                    return "xx";
                }
            }

            public String method(String str) {
                if (str != null && str.length() > 0 && str.charAt(str.length()-1)==',') {
                    str = str.substring(0, str.length()-1);
                }
                return str;
            }

            @Override
            public int getItemCount() {
                return mValues.size();
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                public final View mView;
                public final TextView Contact,Subject,Content,time;
                ImageView imageDrawable;
                public SingletonClass mItem;
                LinearLayout background;
                ImageButton starred;

                public ViewHolder(View view) {
                    super(view);
                    mView = view;
                    Contact = (TextView) view.findViewById(R.id.Contact);
                    Subject = (TextView) view.findViewById(R.id.Subject);
                    Content = (TextView) view.findViewById(R.id.content);
                    imageDrawable=(ImageView) view.findViewById(R.id.imageDrawable);
                    time=(TextView)view.findViewById(R.id.time);
                    background=(LinearLayout)view.findViewById(R.id.background);
                    starred=(ImageButton)view.findViewById(R.id.star);
                    starred.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                }


                @Override
                public String toString() {
                    return super.toString() + " '" + Content.getText() + "'";
                }
            }
        }
}
