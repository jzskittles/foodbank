package technovations.ajuj.technovations2017;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class TextMessage extends AppCompatActivity {

    Button buttonSend;
    String phoneNo, sms;
    String uniqueID;

    SessionManagement session;
    String username, URL;
    private StringRequest request;
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();
        requestQueue = Volley.newRequestQueue(this);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("phoneNo", null, "sms message", null, null);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        phoneNo = "2817776962";
        sms = "Hi! I am a message.";

        Intent in = getIntent();
        uniqueID = in.getStringExtra("message");

        URL = "https://2017ajuj.000webhostapp.com/textMessage.php";
        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
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
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                                            Toast.LENGTH_LONG).show();
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
                        hashMap.put("uniqueid", uniqueID);

                        return hashMap;
                    }

                };

                requestQueue.add(request);

            }
        });





    }

}