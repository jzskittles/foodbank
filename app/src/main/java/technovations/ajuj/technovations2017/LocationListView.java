package technovations.ajuj.technovations2017;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class LocationListView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ListView mainListView;
    private LocationItemAdapter listAdapter;
    private RequestQueue requestQueue;
    private StringRequest request;
    private String maps_url = "https://2017ajuj.000webhostapp.com/MapsTest.php";
    private ArrayList<LocationItem> allLocations = new ArrayList<LocationItem>();
    private static Context context;
    private SessionManagement session;
    private static String address;
    public static AssetManager mgr;
    private ProgressDialog pd;
    private String dorr;
    private String username1, receiver;
    private TextView navDrawerStudentName, navDrawerStudentUsername, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list_view);
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

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_welcome_nav, null);
        navigationView.addHeaderView(header);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();
        LocationListView.context = getApplicationContext();
        //pd = new ProgressDialog(LocationListView.this);
        //pd.setTitle("Loading");
        //pd.setMessage("Please wait...");
        //pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //pd.setCancelable(false);


        requestQueue = Volley.newRequestQueue(this);
        getReviewedContent(context, maps_url);

        mgr = getAssets();
        HashMap<String, String> user = session.getUserDetails();
        dorr = user.get(SessionManagement.KEY_DORR);
        username1 = user.get(SessionManagement.KEY_USERNAME);
        address = user.get(SessionManagement.KEY_ADDRESS);
        receiver = user.get(SessionManagement.KEY_USERNAME);
        String name = user.get(SessionManagement.KEY_NAME);
        dorr = user.get(SessionManagement.KEY_DORR);

        Typeface face2 = Typeface.createFromAsset(getAssets(), "c_gothic.ttf");

        description = (TextView)findViewById(R.id.description);
        description.setTypeface(face2);

        if(dorr.equals("receiver"))
            description.setText("Nearby Donors");
        else
            description.setText("Nearby Receivers");


        navDrawerStudentName = (TextView) header.findViewById(R.id.navDrawerName);
        navDrawerStudentUsername = (TextView) header.findViewById(R.id.navDrawerUsername);

        navDrawerStudentName.setText(name);
        navDrawerStudentUsername.setText(receiver);



        mainListView = (ListView) findViewById(R.id.ListView01);
        listAdapter = new LocationItemAdapter(this, allLocations);
        mainListView.setAdapter(listAdapter);
    }

    public static Context getAppContext()
    {
        return LocationListView.context;
    }

    public static String getMyAddress()
    {
        return address;
    }

    public void sortListByDistance(){
        Collections.sort(allLocations, new Comparator<LocationItem>() {
            @Override
            public int compare(LocationItem lhs, LocationItem rhs) {
                return lhs.getDistanceVal() < rhs.getDistanceVal() ? -1 : lhs.getDistanceVal() > rhs.getDistanceVal()? 1 : 0;
            }
        });
        listAdapter.notifyDataSetChanged();

    }

    public void locationRedirect(View v){
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        TextView tv = (TextView)vwParentRow.getChildAt(0);
        TextView tv2 = (TextView)vwParentRow.getChildAt(2);
        String user = tv.getText().toString();
        String donorrec = tv2.getText().toString();

        if(donorrec.equals("Receiver"))
        {
            Intent i = new Intent(this, ProfileReceiver.class);
            Bundle bundle = new Bundle();
            bundle.putString("username", user);
            i.putExtras(bundle);

            startActivity(i);
        }
        else {
            Intent i = new Intent(this, ProfileDonor.class);
            Bundle bundle = new Bundle();
            bundle.putString("username", user);
            i.putExtras(bundle);

            startActivity(i);
        }

    }

    public static AssetManager giveAssets()
    {
        return mgr;
    }


    public void getReviewedContent(Context con, String url) {
        //pd.show();
        request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)  {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ((jsonObject.getString("success")).equals("data received")) {
                        int count = 0;
                        HashMap<String, String> user = session.getUserDetails();
                        String me = user.get(SessionManagement.KEY_DORR);
                        while (!(jsonObject.optJSONArray("" + count) == null)){
                            JSONArray row = jsonObject.getJSONArray("" + count);
                            count++;
                            if(me.equals("donor")) {
                                if (row.getString(4).equals("receiver")) {
                                    LocationItem item = new LocationItem();
                                    String orgname = row.getString(0);
                                    item.setOrg(orgname);
                                    String address = row.getString(1);
                                    item.setAddress(address);
                                    String usernm = row.getString(3);
                                    item.setUsername(usernm);
                                    item.setDistance();
                                    allLocations.add(item);
                                    sortListByDistance();
                                    listAdapter.notifyDataSetChanged();
                                }
                            }
                            else
                            {
                                if(row.getString(4).equals("donor"))
                                {
                                    LocationItem item = new LocationItem();
                                    String orgname = row.getString(0);
                                    item.setOrg(orgname);
                                    String address = row.getString(1);
                                    item.setAddress(address);
                                    String usernm = row.getString(3);
                                    item.setUsername(usernm);
                                    item.setDistance();
                                    allLocations.add(item);
                                    sortListByDistance();
                                    listAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    } else
                        Toast.makeText(getApplicationContext(), "ERROR: " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                //onMapReady(mMap);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }

        });

        requestQueue.add(request);
        //pd.dismiss();
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
        if (id == R.id.nav_home_location_list_view) {
            if(dorr.equals("donor")){
                Toast.makeText(getApplicationContext(),"Welcome Donor",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), WelcomeDonor.class));
            }else{
                Toast.makeText(getApplicationContext(),"Welcome Receiver",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), WelcomeReceiver.class));
            } }else if (id == R.id.nav_profile_location_list_view) {
            //Toast.makeText(getApplicationContext(),"Profile Receiver",Toast.LENGTH_SHORT).show();
            if(dorr.equals("donor")){
                //Toast.makeText(getApplicationContext(),"Profile Donor",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, ProfileDonor.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", username1);
                i.putExtras(bundle);

                startActivity(i);
                //startActivity(new Intent(getApplicationContext(), ProfileDonor.class));
            }else{

                Intent i = new Intent(this, ProfileReceiver.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", username1);
                i.putExtras(bundle);
                startActivity(i);
            } }else if (id == R.id.nav_userlocations_location_list_view){
            startActivity(new Intent(getApplicationContext(), LocationListView.class));
        }
        else if (id == R.id.nav_logout_location_list_view) {
            session.logoutUser();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}