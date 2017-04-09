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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

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


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_welcome_nav, null);
        navigationView.addHeaderView(header);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();

        statusUsername = (TextView) findViewById(R.id.username_status);
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManagement.KEY_USERNAME);
        //user, name, orgname, address, phoneNumber, email, dorr

        statusUsername.setText(Html.fromHtml(username+""));
        statusMessage = (EditText) findViewById(R.id.status);
        vegetable = (CheckBox) findViewById(R.id.vegetable_status);
        dairy = (CheckBox) findViewById(R.id.dairy_status);
        meat = (CheckBox) findViewById(R.id.meat_status);
        bread = (CheckBox) findViewById(R.id.bread_status);
        fats = (CheckBox) findViewById(R.id.fats_status);

        statustags = new ArrayList<String>();

        Cache cache1 = new DiskBasedCache(getCacheDir(),1024 * 1024);

        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache1, network);

        requestQueue.start();

        listView = (ListView) findViewById(R.id.list);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);
        submit = (Button) findViewById(R.id.submit_status);
        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                request = new StringRequest(Request.Method.POST, "https://2017ajuj.000webhostapp.com/submitstatus1.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                        try{
                            Toast.makeText(getApplicationContext(), "successfully inside the try", Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
                            if(jsonObject.has("success")){
                                statusMessage.setText("");
                                
                                Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"ERROR: "+jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                            }
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
                        hashMap.put("user",statusUsername.getText().toString());
                        hashMap.put("timestamps",getCurrentTimeStamp());
                        hashMap.put("statustext",statusMessage.getText().toString());
                        hashMap.put("interests",statustags.toString());
                        hashMap.put("statuses","pending");
                        return hashMap;
                    }

                };

                requestQueue.add(request);
            }
        });

        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
        /*getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
*/
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
            JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
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

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setName(feedObj.getString("username"));
                item.setId(Integer.parseInt(feedObj.getString("id")));
                // Image might be null sometimes
                item.setStatus(feedObj.getString("statustext"));
                item.setTimeStamp(feedObj.getString("timestamps"));
                item.setUrl(feedObj.getString("interests"));
                // url might be null sometimes

                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.nav_profile){
            startActivity(new Intent(getApplicationContext(), Profile.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
