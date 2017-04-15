package technovations.ajuj.technovations2017;

/**
 * Created by jenny on 4/13/2017.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileDonor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView profileUsername, profileName, profileEmail, profileNumber, profileAddress, profileOrganization;
    private TextView navDrawerStudentName, navDrawerStudentUsername;
    private ListView listView;
    private FeedListAdapter currentAdapter, claimedAdapter;
    private List<FeedItem> claimedItems, currentItems;
    private String URL_FEED = "https://2017ajuj.000webhostapp.com/status.json";
    private EditText statusMessage;
    private SessionManagement session;
    private TextView statusUsername;
    private RequestQueue requestQueue,requestQueue1;
    private StringRequest request;
    private CheckBox vegetable, dairy, meat, bread, fats;
    private ArrayList<String> statustags;
    private static final String TAG = ProfileDonor.class.getSimpleName();
    String username1, name, orgname, address, email, dorr;
    int phoneNumber;
    private TabHost tabhost;

    private ListView tab1, tab2;


    /*  String URL = "http://ajuj.comlu.com/checkHours.php";
    private RequestQueue requestQueue;
    private StringRequest request; */
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_donor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final String username;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                username= null;
            } else {
                username= extras.getString("username");
            }
        } else {
            username= (String) savedInstanceState.getSerializable("username");
        }
        requestQueue1 = Volley.newRequestQueue(this);

        profileUsername = (TextView) findViewById(R.id.profileUsername);
        profileName = (TextView) findViewById(R.id.profileName);
        profileEmail = (TextView) findViewById(R.id.profileEmail);
        profileNumber= (TextView) findViewById(R.id.profileNumber);
        profileAddress= (TextView) findViewById(R.id.profileAddress);
        profileOrganization = (TextView) findViewById(R.id.profileOrganization);


        request = new StringRequest(Request.Method.POST, "https://2017ajuj.000webhostapp.com/fetchUser.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")) {
                        //Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                        //username1 = jsonObject.getString("user");
                        /*name = jsonObject.getString("name");
                        orgname = jsonObject.getString("orgname");
                        address = jsonObject.getString("address");
                        phoneNumber = jsonObject.getInt("phoneNumber");
                        email = jsonObject.getString("email");
                        dorr = jsonObject.getString("dorr");*/
                        profileUsername.setText(Html.fromHtml("<b>User: </b>" + username));
                        profileName.setText(Html.fromHtml("<b>Name: </b>" + jsonObject.getString("name")));
                        profileEmail.setText(Html.fromHtml("<b>Email: </b>" + jsonObject.getString("email")));
                        profileOrganization.setText(Html.fromHtml("<b>Organization: </b>" + jsonObject.getString("orgname")));
                        profileNumber.setText(Html.fromHtml("<b>Phone Number: </b> " + jsonObject.getString("phoneNumber")));
                        profileAddress.setText(Html.fromHtml("<b>Address: </b> " + jsonObject.getString("address")));
                    }
                    else
                        Toast.makeText(getApplicationContext(),"ERROR: "+jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap=new HashMap<String,String>();
                hashMap.put("user",username);
                return hashMap;
            }
        };
        requestQueue1.add(request);

        session = new SessionManagement(getApplicationContext());

        tabhost = (TabHost) findViewById(R.id.tabHost);
        tabhost.setup();
        TabHost.TabSpec current = tabhost.newTabSpec("Current");
        TabHost.TabSpec claimed = tabhost.newTabSpec("Claimed");

        TabWidget t = (TabWidget) findViewById(android.R.id.tabs);
        for(int i=0;i<t.getChildCount();i++){
            t.getChildAt(i).setBackgroundResource(R.drawable.log_tabs);
        }

        requestQueue = Volley.newRequestQueue(this);

        tab1 = (ListView) findViewById(R.id.list_current);
        tab2 = (ListView) findViewById(R.id.list_claimed);

        claimedItems = new ArrayList<FeedItem>();
        currentItems = new ArrayList<FeedItem>();

        claimedAdapter = new FeedListAdapter(this, claimedItems);
        tab2.setAdapter(claimedAdapter);
        currentAdapter = new FeedListAdapter(this, currentItems);
        tab1.setAdapter(currentAdapter);

        current.setIndicator("Current");
        current.setContent(tab1.getId());
        claimed.setIndicator("Claimed");
        claimed.setContent(tab2.getId());
        tabhost.addTab(current);
        tabhost.addTab(claimed);

        for(int i = 0; i <tabhost.getTabWidget().getChildCount(); i++)
        {
            if(i==0)
            {
                tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#00000000"));
                TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(Color.parseColor("#FFFFFF"));
            }
            else
            {
                tabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.log_tabs);
                TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
                for(int i=0;i<tabhost.getTabWidget().getChildCount();i++)
                {
                    tabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.log_tabs); //unselected
                }
                tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#00000000")); // selected
            }
        });


        Cache cache1 = new DiskBasedCache(getCacheDir(),1024 * 1024);

        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache1, network);

        requestQueue.start();




        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();



        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        final String username1 = user.get(SessionManagement.KEY_USERNAME);
        String sname = user.get(SessionManagement.KEY_NAME);
        String email = user.get(SessionManagement.KEY_EMAIL);
        String orgname = user.get(SessionManagement.KEY_ORGNAME);
        String address = user.get(SessionManagement.KEY_ADDRESS);
        int phoneNumber = Integer.parseInt(user.get(SessionManagement.KEY_PHONENUMBER));
        dorr = user.get(SessionManagement.KEY_DORR);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_welcome_nav, null);
        navigationView.addHeaderView(header);


        navDrawerStudentName = (TextView) header.findViewById(R.id.navDrawerName);
        navDrawerStudentUsername = (TextView) header.findViewById(R.id.navDrawerUsername);

        navDrawerStudentName.setText(sname);
        navDrawerStudentUsername.setText(username1);




        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data), username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    URL_FEED, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response, username);
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

    private void parseJsonFeed(JSONObject response, String user) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");
            String receivers="";

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setName(feedObj.getString("username"));
                if(feedObj.getString("username").equals(user)) {
                    item.setId(feedObj.getInt("id"));

                    // Image might be null sometimes
                /*String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);*/
                    //receiver = feedObj.getString("receiver");
                    item.setStatus(feedObj.getString("statustext"));
                    //item.setProfilePic(feedObj.getString("profilePic"));
                    item.setTimeStamp(feedObj.getString("timestamps"));

                    // url might be null sometimes
                    item.setUrl(feedObj.getString("interests"));
                    item.setUid(feedObj.getString("uid"));

                    // url might be null sometimes
                    String feedUrl = feedObj.isNull("url") ? null : feedObj
                            .getString("url");
                    item.setUrl(feedUrl);
                    item.setReceiver(feedObj.getString("receiver"));
                    receivers = feedObj.getString("receiver");
                    if (feedObj.getString("receiver").equals("")) {
                        currentItems.add(item);
                        currentAdapter.notifyDataSetChanged();
                    } else {
                        claimedItems.add(item);
                        claimedAdapter.notifyDataSetChanged();
                    }
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
        if (id == R.id.nav_home_profile_donor) {
            if(dorr.equals("donor")){
                Toast.makeText(getApplicationContext(),"Welcome Donor",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), WelcomeDonor.class));
            }else
                startActivity(new Intent(getApplicationContext(), WelcomeReceiver.class));
        } else if (id == R.id.nav_profile_profile_donor) {
            Toast.makeText(getApplicationContext(),"Profile Receiver",Toast.LENGTH_SHORT).show();
            if(dorr.equals("donor")){
                Toast.makeText(getApplicationContext(),"Profile Donor",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, ProfileDonor.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", username1);
                i.putExtras(bundle);

                startActivity(i);
                //startActivity(new Intent(getApplicationContext(), ProfileDonor.class));
            }else
                startActivity(new Intent(getApplicationContext(), ProfileReceiver.class));
        } else if (id == R.id.nav_logout_profile_donor) {
            session.logoutUser();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static String getCurrentTimeStamp(){
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());
            return currentDateTime;
        }catch (Exception e){
            e.printStackTrace();

            return null;
        }
    }
}
