package technovations.ajuj.technovations2017;


import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.technovations.innova.technovations2.*;
//import com.technovations.innova.technovations2.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileReceiver extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManagement session;
    TextView profileUsername, profileName, profileEmail, profileNumber, profileAddress, profileOrganization;
    private TextView navDrawerStudentName, navDrawerStudentUsername;
    String username;
    private RequestQueue requestQueue;
    private static final String TAG = ProfileReceiver.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "https://2017ajuj.000webhostapp.com/status.json";
    private Button vegetables, dairy, meat, bread, fats;


  /*  String URL = "http://ajuj.comlu.com/checkHours.php";
    private RequestQueue requestQueue;
    private StringRequest request; */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_receiver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        session = new SessionManagement(getApplicationContext());
        profileUsername = (TextView) findViewById(R.id.profileUsername);
        profileName = (TextView) findViewById(R.id.profileName);
        profileEmail = (TextView) findViewById(R.id.profileEmail);
        profileNumber= (TextView) findViewById(R.id.profileNumber);
        profileAddress= (TextView) findViewById(R.id.profileAddress);
        profileOrganization = (TextView) findViewById(R.id.profileOrganization);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        username = user.get(SessionManagement.KEY_USERNAME);
        final String username1 = user.get(SessionManagement.KEY_USERNAME);
        String name = user.get(SessionManagement.KEY_NAME);
        String email = user.get(SessionManagement.KEY_EMAIL);
        String orgname = user.get(SessionManagement.KEY_ORGNAME);
        String address = user.get(SessionManagement.KEY_ADDRESS);
        int phoneNumber = Integer.parseInt(user.get(SessionManagement.KEY_PHONENUMBER));
        String dorr = user.get(SessionManagement.KEY_DORR);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_welcome_nav, null);
        navigationView.addHeaderView(header);


        navDrawerStudentName = (TextView) header.findViewById(R.id.navDrawerName);
        navDrawerStudentUsername = (TextView) header.findViewById(R.id.navDrawerUsername);

        navDrawerStudentName.setText(name);
        navDrawerStudentUsername.setText(username);

        profileUsername.setText(Html.fromHtml("<b>User: </b>" + username));
        profileName.setText(Html.fromHtml("<b>Name: </b>" + name));
        profileEmail.setText(Html.fromHtml("<b>Email: </b>" + email));
        profileOrganization.setText(Html.fromHtml("<b>Organization: </b>" + orgname));
        profileNumber.setText(Html.fromHtml("<b>Phone Number: </b> " + phoneNumber));
        profileAddress.setText(Html.fromHtml("<b>Address: </b> " + address));

        vegetables = (Button)findViewById(R.id.vegetable_button);
        dairy = (Button)findViewById(R.id.dairy_button);
        meat = (Button)findViewById(R.id.meat_button);
        bread = (Button)findViewById(R.id.bread_button);
        fats = (Button)findViewById(R.id.fats_button);

        listView = (ListView) findViewById(R.id.list_claim);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);




        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    "https://2017ajuj.000webhostapp.com/status.json", new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    //Toast.makeText(getApplicationContext(),response+"",Toast.LENGTH_LONG).show();
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }

    }
    public void statusSort(View v){
        //Toast.makeText(getApplicationContext(), "You don't have this function", Toast.LENGTH_SHORT).show();
        LinearLayout vw = (LinearLayout)v.getParent();
        Button vegetable = (Button)vw.getChildAt(0);
        vegetable.setOnClickListener(null);
        Button dairy = (Button)vw.getChildAt(0);
        dairy.setOnClickListener(null);
        Button meat = (Button)vw.getChildAt(0);
        meat.setOnClickListener(null);
        Button bread = (Button)vw.getChildAt(0);
        bread.setOnClickListener(null);
        Button fats = (Button)vw.getChildAt(0);
        fats.setOnClickListener(null);
    }

    public void profileRedirect(View v){
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        TextView tv = (TextView)vwParentRow.getChildAt(1);
        String user = tv.getText().toString();

        Intent i = new Intent(this, ProfileDonor.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", user);
        i.putExtras(bundle);

        startActivity(i);
    }

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");
            String receivers="";
            //Toast.makeText(getApplicationContext(),""+feedArray.length(),Toast.LENGTH_LONG).show();

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                //Toast.makeText(getApplicationContext(),""+feedObj.getInt("id")+feedObj.getString("receiver"),Toast.LENGTH_SHORT).show();
                if(feedObj.getString("receiver").equals(username)){
                item.setReceiver(feedObj.getString("receiver"));
                //if(feedObj.getString("receiver").isEmpty()) {
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("username"));


                    // Image might be null sometimes
                /*String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);*/
                    //receiver = feedObj.getString("receiver");
                    item.setStatus(feedObj.getString("statustext"));
                    //item.setProfilePic(feedObj.getString("profilePic"));
                    item.setTimeStamp(feedObj.getString("timestamps"));

                    // url might be null sometimes
                    //item.setUrl(feedObj.getString("interests"));
                    item.setUid(feedObj.getString("uid"));

                    // url might be null sometimes
                    String feedUrl = feedObj.isNull("url") ? null : feedObj
                            .getString("url");
                    //item.setUrl(feedUrl);
                    String interests = feedObj.getString("interests");
                    String[] strings = interests.replace("[", "").replace("]", "").split(", ");
                    if(strings[0].equals("true")){
                        item.setVegetables(true);
                    }else{
                        item.setVegetables(false);
                    }if(strings[1].equals("true")){
                        item.setDairy(true);
                    }else{
                        item.setDairy(false);
                    }if(strings[2].equals("true")){
                        item.setMeat(true);
                    }else{
                        item.setMeat(false);
                    }if(strings[3].equals("true")){
                        item.setBread(true);
                    }else{
                        item.setBread(false);
                    }if(strings[4].equals("true")){
                        item.setFats(true);
                    }else{
                        item.setFats(false);
                    }
                    receivers = feedObj.getString("receiver");
                        feedItems.add(item);
                        listAdapter.notifyDataSetChanged();

                }

            }

            // notify data changes to list adapater

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home_profile_receiver) {
            //Toast.makeText(getApplicationContext(),"Welcome Receiver",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), WelcomeReceiver.class));
        } else if (id == R.id.nav_profile_profile_receiver) {
            //Toast.makeText(getApplicationContext(),"Profile Receiver",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), ProfileReceiver.class));
        } else if (id == R.id.nav_logout_profile_receiver) {
            session.logoutUser();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}