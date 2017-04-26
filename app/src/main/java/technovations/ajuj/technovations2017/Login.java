package technovations.ajuj.technovations2017;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import technovations.ajuj.technovations2017.*;
//import com.technovations.innova.technovations2.Register;
import technovations.ajuj.technovations2017.SessionManagement;
//import technovations.ajuj.technovations2017.WelcomeNav;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jenny on 3/12/2017.
 */

public class Login extends AppCompatActivity{

    private EditText username,password;
    private Button sign_in;
    private Button register;
    private RequestQueue requestQueue;
    technovations.ajuj.technovations2017.SessionManagement session;

    private String URL = ""; //changeable
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView tv = (TextView)findViewById(R.id.textView);
        TextView tv2 = (TextView)findViewById(R.id.textView2);
        Typeface face = Typeface.createFromAsset(getAssets(), "QueenofHeaven.ttf");
        Typeface face2 = Typeface.createFromAsset(getAssets(), "c_gothic.ttf");
        tv.setTypeface(face);
        tv2.setTypeface(face2);

        username = (EditText)findViewById(R.id.username);
        username.setTypeface(face2);
        password = (EditText)findViewById(R.id.password);
        password.setTypeface(face2);
        sign_in = (Button)findViewById(R.id.sign_in);
        sign_in.setTypeface(face2);
        register = (Button)findViewById(R.id.register);
        register.setTypeface(face2);

        requestQueue = Volley.newRequestQueue(this);

        session = new SessionManagement(getApplication());


        URL = "https://2017ajuj.000webhostapp.com/login.php";


        sign_in.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("success")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                String user = jsonObject.getString("user");
                                String name = jsonObject.getString("name");
                                String orgname = jsonObject.getString("orgname");
                                String address = jsonObject.getString("address");
                                int phoneNumber = jsonObject.getInt("phoneNumber");
                                String propic = jsonObject.getString("propic");
                                String email = jsonObject.getString("email");
                                String dorr = jsonObject.getString("dorr");


                                session.createLoginSession(user, name, orgname, address, phoneNumber, propic, email, dorr);
                                if(dorr.equals("donor"))
                                    startActivity(new Intent(getApplicationContext(), WelcomeDonor.class));
                                if(dorr.equals("receiver"))
                                    startActivity(new Intent(getApplicationContext(), WelcomeReceiver.class));

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
                        hashMap.put("username",username.getText().toString());
                        hashMap.put("password",password.getText().toString());

                        return hashMap;
                    }

                };

                requestQueue.add(request);
            }
        });

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });
    }


}