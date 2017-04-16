package technovations.ajuj.technovations2017;

/**
 * Created by jenny on 3/23/2017.
 */

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class WelcomeReceiver extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = WelcomeReceiver.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "https://2017ajuj.000webhostapp.com/status.json";
    private SessionManagement session;
    private TextView statusUsername;
    private RequestQueue requestQueue;
    private StringRequest request;
    private ArrayList<String> statustags;
    private String dorr, receiver;
    private ArrayList<String> uids;
    private TextView navDrawerStudentName, navDrawerStudentUsername;
    String phoneNo, sms;
    String uniqueID;
    /*IntentFilter intentFilter;
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //---display the SMS received in the TextView---
            //TextView SMSes = (TextView) findViewById(R.id.textView1);
            Toast.makeText(getApplicationContext(), intent.getExtras().getString("sms"), Toast.LENGTH_LONG).show();
        }
    };*/


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_receiver);
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
        session.checkLogin();

        statusUsername = (TextView) findViewById(R.id.username_status);
        HashMap<String, String> user = session.getUserDetails();
        receiver = user.get(SessionManagement.KEY_USERNAME);
        String name = user.get(SessionManagement.KEY_NAME);
        dorr = user.get(SessionManagement.KEY_DORR);
        //user, name, orgname, address, phoneNumber, email, dorr

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_welcome_nav, null);
        navigationView.addHeaderView(header);
        navDrawerStudentName = (TextView) header.findViewById(R.id.navDrawerName);
        navDrawerStudentUsername = (TextView) header.findViewById(R.id.navDrawerUsername);

        navDrawerStudentName.setText(name);
        navDrawerStudentUsername.setText(receiver);

        //SmsManager smsManager = SmsManager.getDefault();
        //smsManager.sendTextMessage("phoneNo", null, "sms message", null, null);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);

        /*intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);*/



        statustags = new ArrayList<String>();
        uids = new ArrayList<String>();

        Cache cache1 = new DiskBasedCache(getCacheDir(), 1024 * 1024);

        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache1, network);

        requestQueue.start();

        listView = (ListView) findViewById(R.id.list_claim);


        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);

        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(URL_FEED);
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
                    URL_FEED, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
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
    // These two lines not needed,
    // just to get the look of facebook (changing background color & hiding the icon)
        /*getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
*/
    // We first check for cached request

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

    public void claimListener(View v){
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        LinearLayout child1 = (LinearLayout)vwParentRow.getChildAt(0);
        LinearLayout child2 = (LinearLayout)child1.getChildAt(1);
        TextView tv = (TextView)child2.getChildAt(0);
        final String uid = tv.getText().toString();
        Button btnChild = (Button)vwParentRow.getChildAt(4);
        request = new StringRequest(Request.Method.POST, "https://2017ajuj.000webhostapp.com/claim.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                try {
                    //Toast.makeText(getApplicationContext(), "successfully inside the try", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("successUpdate")) {
                        Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("successUpdate"), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("successInsert"), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("successDelete"), Toast.LENGTH_SHORT).show();
                    } else {
                        /*Toast.makeText(getApplicationContext(), "ERROR: " + jsonObject.getString("errorInsert"), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "ERROR: " + jsonObject.getString("successUpdate"), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "ERROR: " + jsonObject.getString("successInsert"), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "ERROR: " + jsonObject.getString("successDelete"), Toast.LENGTH_SHORT).show();*/
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
        listAdapter.notifyDataSetChanged();

        request = new StringRequest(Request.Method.POST, "https://2017ajuj.000webhostapp.com/textMessage.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")) {
                        Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                        String user = jsonObject.getString("user");
                        String interests = jsonObject.getString("interests");
                        String receiver = jsonObject.getString("receiver");
                        phoneNo = jsonObject.getString("phoneNumber");

                        sms = "Message to " + user + " with phoneNumber " + phoneNo + ": " + receiver + " has claimed your " + interests;

                        try {
                            String SMS_SENT = "SMS_SENT";
                            String SMS_DELIVERED = "SMS_DELIVERED";

                            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SMS_SENT), 0);
                            PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SMS_DELIVERED), 0);

// For when the SMS has been sent
                            registerReceiver(new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    switch (getResultCode()) {
                                        case Activity.RESULT_OK:
                                            Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                                            break;
                                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                            Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                                            break;
                                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                                            Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                                            break;
                                        case SmsManager.RESULT_ERROR_NULL_PDU:
                                            Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                                            break;
                                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                                            Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            }, new IntentFilter(SMS_SENT));

// For when the SMS has been delivered
                            registerReceiver(new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    switch (getResultCode()) {
                                        case Activity.RESULT_OK:
                                            Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                                            break;
                                        case Activity.RESULT_CANCELED:
                                            Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            }, new IntentFilter(SMS_DELIVERED));

// Get the default instance of SmsManager
                            SmsManager smsManager = SmsManager.getDefault();
// Send a text based SMS
                            smsManager.sendTextMessage(phoneNo, null, sms, sentPendingIntent, deliveredPendingIntent);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "SMS failed, please try again later!",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }



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
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap=new HashMap<String,String>();
                hashMap.put("uniqueid", uid);

                return hashMap;
            }

        };

        requestQueue.add(request);
    }


    private void parseJsonFeed(JSONObject response){
        try{
            JSONArray feedArray=response.getJSONArray("feed");

            for(int i=0;i<feedArray.length();i++){
                JSONObject feedObj=(JSONObject)feedArray.get(i);

                FeedItem item=new FeedItem();
                if(feedObj.getString("receiver").isEmpty()) {
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("username"));

                    // Image might be null sometimes
                /*String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);*/
                    //receiver = feedObj.getString("receiver");
                    uids.add(feedObj.getString("uid"));
                    item.setStatus(feedObj.getString("statustext"));
                    //item.setProfilePic(feedObj.getString("profilePic"));
                    item.setTimeStamp(feedObj.getString("timestamps"));

                    // url might be null sometimes
                    item.setUrl(feedObj.getString("interests"));
                    item.setUid(feedObj.getString("uid"));
                    item.setDorr(dorr);

                    feedItems.add(item);
                }
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
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
        if (id == R.id.nav_home_welcome_receiver) {
            //Toast.makeText(getApplicationContext(),"Welcome Receiver",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), WelcomeReceiver.class));
        } else if (id == R.id.nav_profile_welcome_receiver) {
            //Toast.makeText(getApplicationContext(),"Profile Receiver",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), ProfileReceiver.class));
        } else if (id == R.id.nav_logout_welcome_receiver) {
            //Toast.makeText(getApplicationContext(),"logout Receiver",Toast.LENGTH_SHORT).show();
            session.logoutUser();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void selectItem(View view){
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.vegetable_status:
                if(checked){
                    statustags.add("Vegetables");}
                else{
                    statustags.remove("Vegetables");}
                break;
            case R.id.dairy_status:
                if(checked){
                    statustags.add("Milk, Yogurt, Cheese");}
                else{
                    statustags.remove("Milk, Yogurt, Cheese");}
                break;
            case R.id.meat_status:
                if(checked){
                    statustags.add("Meat, Poultry, Fish, Beans, Eggs, Nuts");}
                else{
                    statustags.remove("Meat, Poultry, Fish, Beans, Eggs, Nuts");}
                break;
            case R.id.bread_status:
                if(checked){
                    statustags.add("Bread, Cereal, Rice, Pasta");}
                else{
                    statustags.remove("Bread, Cereal, Rice, Pasta");}
                break;
            case R.id.fats_status:
                if(checked){
                    statustags.add("Fats, Oil, Sweets");}
                else{
                    statustags.remove("Fats, Oil, Sweets");}
                break;
        }
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

    /*@Override
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

    }*/
}
