package technovations.ajuj.technovations2017;

/**
 * Created by jenny on 3/23/2017.
 */

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
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

import static android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;

public class WelcomeDonor extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = WelcomeDonor.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "https://2017ajuj.000webhostapp.com/status.json";
    private EditText statusMessage;
    private SessionManagement session;
    private TextView statusUsername;
    private Button submit;
    private RequestQueue requestQueue;
    private StringRequest request;
    private CheckBox vegetable, dairy, meat, bread, fats;
    private ArrayList<String> statustags;
    private String receiver, propic;
    private TextView navDrawerStudentName, navDrawerStudentUsername;
    IntentFilter intentFilter;
    private Button vegetables_button, dairy_button, meat_button, bread_button, fats_button;
    String dorr;

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //---display the SMS received in the TextView---
            //TextView SMSes = (TextView) findViewById(R.id.textView1);
            Toast.makeText(getApplicationContext(), intent.getExtras().getString("sms"), Toast.LENGTH_LONG).show();
        }
    };


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_donor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        session = new SessionManagement(getApplicationContext());


        statusUsername = (TextView) findViewById(R.id.username_status);
        HashMap<String, String> user = session.getUserDetails();
        String sname = user.get(SessionManagement.KEY_NAME);
        receiver = user.get(SessionManagement.KEY_USERNAME);
        propic = user.get(SessionManagement.KEY_PROPIC);
        dorr = user.get(SessionManagement.KEY_DORR);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_welcome_nav, null);
        navigationView.addHeaderView(header);


        navDrawerStudentName = (TextView) header.findViewById(R.id.navDrawerName);
        navDrawerStudentUsername = (TextView) header.findViewById(R.id.navDrawerUsername);

        navDrawerStudentName.setText(sname);
        navDrawerStudentUsername.setText(receiver);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();

        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);


        //user, name, orgname, address, phoneNumber, email, dorr

        statusUsername.setText(Html.fromHtml(receiver + ""));
        statusMessage = (EditText) findViewById(R.id.status);
        vegetable = (CheckBox) findViewById(R.id.vegetable_status);
        dairy = (CheckBox) findViewById(R.id.dairy_status);
        meat = (CheckBox) findViewById(R.id.meat_status);
        bread = (CheckBox) findViewById(R.id.bread_status);
        fats = (CheckBox) findViewById(R.id.fats_status);

        statustags = new ArrayList<String>();
        statustags.add(0,"false");
        statustags.add(1,"false");
        statustags.add(2,"false");
        statustags.add(3,"false");
        statustags.add(4,"false");

        vegetables_button = (Button)findViewById(R.id.vegetable_button);
        dairy_button = (Button)findViewById(R.id.dairy_button);
        meat_button = (Button)findViewById(R.id.meat_button);
        bread_button = (Button)findViewById(R.id.bread_button);
        fats_button = (Button)findViewById(R.id.fats_button);



        Cache cache1 = new DiskBasedCache(getCacheDir(), 1024 * 1024);

        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache1, network);

        requestQueue.start();

        listView = (ListView) findViewById(R.id.list);


        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);
        submit = (Button) findViewById(R.id.submit_status);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                request = new StringRequest(Request.Method.POST, "https://2017ajuj.000webhostapp.com/submitstatus1.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                        try {
                            //Toast.makeText(getApplicationContext(), "successfully inside the try", Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
                            statusMessage.setText("");
                            vegetable.setChecked(false);
                            dairy.setChecked(false);
                            meat.setChecked(false);
                            bread.setChecked(false);
                            fats.setChecked(false);
                            statustags.clear();
                            //Toast.makeText(getApplicationContext(),statustags.toString(), Toast.LENGTH_SHORT).show();
                            /*statustags.set(0,"false");
                            statustags.set(1,"false");
                            statustags.set(2,"false");
                            statustags.set(3,"false");
                            statustags.set(4,"false");*/

                            if (jsonObject.has("success")) {
                                Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                feedItems.clear();
                                getFeed("all");
                            } else {
                                Toast.makeText(getApplicationContext(), "ERROR: " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("user", statusUsername.getText().toString());
                        hashMap.put("timestamps", getCurrentTimeStamp());
                        hashMap.put("statustext", statusMessage.getText().toString());
                        hashMap.put("interests", statustags.toString());
                        hashMap.put("statuses", "pending");
                        hashMap.put("propic", propic);
                        return hashMap;
                    }

                };

                requestQueue.add(request);
            }
        });

        getFeed("all");

        // We first check for cached request
    }
    // These two lines not needed,
    // just to get the look of facebook (changing background color & hiding the icon)
        /*getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
*/
    // We first check for cached request

    public void getFeed(String category){
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(URL_FEED);
        final String type = category;
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data), type);
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
                        parseJsonFeed(response,type);
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

    public void profileRedirect(View v) {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        TextView tv = (TextView) vwParentRow.getChildAt(1);
        String user = tv.getText().toString();

        Intent i = new Intent(this, ProfileDonor.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", user);
        i.putExtras(bundle);

        startActivity(i);

    }

    public void claimListener(View v) {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        LinearLayout child1 = (LinearLayout) vwParentRow.getChildAt(0);
        LinearLayout child2 = (LinearLayout) child1.getChildAt(1);
        TextView tv = (TextView) child2.getChildAt(0);
        final String uid = tv.getText().toString();
        Button btnChild = (Button) vwParentRow.getChildAt(4);
        request = new StringRequest(Request.Method.POST, "https://2017ajuj.000webhostapp.com/claim.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                try {
                    //Toast.makeText(getApplicationContext(), "successfully inside the try", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("successUpdate")) {
                        Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("successUpdate"), Toast.LENGTH_SHORT).show();
                        listAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR: " + receiver + ", " + uid, Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("uid", uid);
                hashMap.put("receiver", receiver);
                return hashMap;
            }

        };

        requestQueue.add(request);
    }

    private void parseJsonFeed(JSONObject response, String type) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
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
                item.setDorr(dorr);
                if (feedObj.getString("receiver").isEmpty()) {
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("username"));

                    // Image might be null sometimes
                /*String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);*/
                    item.setStatus(feedObj.getString("statustext"));
                    item.setProfilePic(feedObj.getString("propic"));
                    //Toast.makeText(getApplicationContext(), feedObj.getString("propic"),Toast.LENGTH_SHORT).show();
                    item.setTimeStamp(feedObj.getString("timestamps"));

                    // url might be null sometimes
                    //item.setUrl(feedObj.getString("interests"));

                    feedItems.add(item);


                    // notify data changes to list adapater
                    listAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        if (id == R.id.nav_home_welcome_donor) {
            startActivity(new Intent(getApplicationContext(), WelcomeDonor.class));
        } else if (id == R.id.nav_profile_welcome_donor) {
            Intent i = new Intent(this, ProfileDonor.class);
            Bundle bundle = new Bundle();
            bundle.putString("username", receiver);
            i.putExtras(bundle);

            startActivity(i);
            //startActivity(new Intent(getApplicationContext(), ProfileDonor.class));
        } else if (id == R.id.nav_userlocations_welcome_donor) {
            //spinner.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), LocationListView.class));
            //spinner.setVisibility(View.GONE);
        }
        else if (id == R.id.nav_logout_welcome_donor) {
            session.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void selectItem(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.vegetable_status:
                if (checked) {
                    statustags.set(0, "true");
                } else {
                    statustags.set(0, "false");
                }
                break;
            case R.id.dairy_status:
                if (checked) {
                    statustags.set(1, "true");
                } else {
                    statustags.set(1, "false");
                }
                break;
            case R.id.meat_status:
                if (checked) {
                    statustags.set(2, "true");
                } else {
                    statustags.set(2, "false");
                }
                break;
            case R.id.bread_status:
                if (checked) {
                    statustags.set(3, "true");
                } else {
                    statustags.set(3, "false");
                }
                break;
            case R.id.fats_status:
                if (checked) {
                    statustags.set(4, "true");
                } else {
                    statustags.set(4, "false");
                }
                break;
        }
    }

    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    protected void onResume() {
        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //---unregister the receiver---
        unregisterReceiver(intentReceiver);
        super.onPause();
    }

    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message) {

    }
}
