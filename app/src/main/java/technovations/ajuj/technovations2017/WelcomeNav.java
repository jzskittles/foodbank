package technovations.ajuj.technovations2017;

/**
 * Created by jenny on 3/12/2017.
 */

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
import android.widget.ImageView;
import android.widget.TextView;

import technovations.ajuj.technovations2017.*;
//import technovations.ajuj.technovations2017.Log;
//import technovations.ajuj.technovations2017.Profile;

import java.util.HashMap;

public class WelcomeNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManagement session;
    private TextView navDrawerStudentName, navDrawerStudentUsername, navDrawerWelcome, hoursStatus;
    private ImageView ppl10, ppl9, ppl8, ppl7, ppl6, ppl5, ppl4, ppl3, ppl2, ppl1, ppl0;
    private Button profileButton, draftsButton, submitButton, logButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManagement.KEY_USERNAME);
        String name = user.get(SessionManagement.KEY_NAME);
        String email = user.get(SessionManagement.KEY_EMAIL);
        int hours = Integer.parseInt(user.get(SessionManagement.KEY_HOURS));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_welcome_nav, null);
        navigationView.addHeaderView(header);

        navDrawerStudentName = (TextView) header.findViewById(R.id.navDrawerStudentName);
        navDrawerStudentUsername = (TextView) header.findViewById(R.id.navDrawerStudentUsername);


        navDrawerWelcome = (TextView) findViewById(R.id.navDrawerWelcome);
        hoursStatus = (TextView) findViewById(R.id.hoursStatus);

        navDrawerStudentName.setText(name); //i   crie
        navDrawerStudentUsername.setText(username);
        navDrawerWelcome.setText(Html.fromHtml("Welcome to VTRACC, <b>" + username + "</b>"));
        hoursStatus.setText(Html.fromHtml("<b>Hours: </b>" + hours));

        profileButton = (Button) findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), technovations.ajuj.technovations2017.Profile.class));
            }
        });
        draftsButton = (Button) findViewById(R.id.draftsButton);
        draftsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), Drafts.class));
            }
        });
        logButton = (Button) findViewById(R.id.logButton);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), Log.class));
            }
        });
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), Create.class));
            }
        });
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
        getMenuInflater().inflate(R.menu.welcome_nav, menu);
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

        if (id == R.id.nav_home) {
            //startActivity(new Intent(getApplicationContext(), WelcomeNav.class));
        } else if (id == R.id.nav_profile) {
            //startActivity(new Intent(getApplicationContext(), Profile.class));
        } else if (id == R.id.nav_create) {
            //startActivity(new Intent(getApplicationContext(), Create.class));
        } else if (id == R.id.nav_drafts) {
            //startActivity(new Intent(getApplicationContext(), Drafts.class));
        } else if (id == R.id.nav_log) {
            //startActivity(new Intent(getApplicationContext(), Log.class));
        } else if (id == R.id.nav_logout) {
            session.logoutUser();
        } /*else if (id == R.id.nav_signature) {
            startActivity(new Intent(getApplicationContext(), Signature.class));
        } else if (id == R.id.nav_view_signature) {
            startActivity(new Intent(getApplicationContext(), viewSignature.class));
        } */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
