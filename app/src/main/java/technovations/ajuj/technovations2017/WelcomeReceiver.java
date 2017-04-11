package technovations.ajuj.technovations2017;

/**
 * Created by jenny on 3/23/2017.
 */
import technovations.ajuj.technovations2017.*;
import technovations.ajuj.technovations2017.FeedListAdapter;
import technovations.ajuj.technovations2017.AppController;
import technovations.ajuj.technovations2017.FeedItem;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
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
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

public class WelcomeReceiver extends Activity{
    private static final String TAG = WelcomeReceiver.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "https://2017ajuj.000webhostapp.com/status.json";
    private EditText statusMessage;
    private SessionManagement session;
    private TextView statusUsername;
    private Button claim;
    private RequestQueue requestQueue;
    private StringRequest request;
    private CheckBox vegetable, dairy, meat, bread, fats;
    private ArrayList<String> statustags;
    List<Map<String, String>> claimedList;
    private ArrayList claimedID;
    private String uniqueIDMessage, receiver;
    private ArrayList<String> uids;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();

        statusUsername = (TextView) findViewById(R.id.username_status);
        HashMap<String, String> user = session.getUserDetails();
        receiver = user.get(SessionManagement.KEY_USERNAME);
        //user, name, orgname, address, phoneNumber, email, dorr


        statustags = new ArrayList<String>();
        uids = new ArrayList<String>();

        Cache cache1 = new DiskBasedCache(getCacheDir(), 1024 * 1024);

        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache1, network);

        requestQueue.start();

        listView = (ListView) findViewById(R.id.list);


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
    public void claimListener(View v){
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        LinearLayout child1 = (LinearLayout)vwParentRow.getChildAt(0);
        LinearLayout child2 = (LinearLayout)child1.getChildAt(1);
        TextView tv = (TextView)child2.getChildAt(0);
        final String uid = tv.getText().toString();
        Button btnChild = (Button)vwParentRow.getChildAt(4);
        btnChild.setText("I've been claimed");
        request = new StringRequest(Request.Method.POST, "https://2017ajuj.000webhostapp.com/claim.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                try {
                    Toast.makeText(getApplicationContext(), "successfully inside the try", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("successUpdate")) {
                        Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("successUpdate"), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("successInsert"), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("successDelete"), Toast.LENGTH_SHORT).show();
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
    }


    private void parseJsonFeed(JSONObject response){
        try{
            JSONArray feedArray=response.getJSONArray("feed");

            for(int i=0;i<feedArray.length();i++){
                JSONObject feedObj=(JSONObject)feedArray.get(i);

                FeedItem item=new FeedItem();
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

                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
}
