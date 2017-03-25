package technovations.ajuj.technovations2017;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import technovations.ajuj.technovations2017.*;
//import com.technovations.innova.technovations2.AdminNav;
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

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        sign_in = (Button)findViewById(R.id.sign_in);
        register = (Button)findViewById(R.id.register);

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

                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("success")) {
                                Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                String user = jsonObject.getString("user");
                                String name = jsonObject.getString("name");
                                String orgname = jsonObject.getString("orgname");
                                String address = jsonObject.getString("address");
                                int phoneNumber = jsonObject.getInt("phoneNumber");
                                String email = jsonObject.getString("email");
                                String dorr = jsonObject.getString("dorr");


                                session.createLoginSession(user, name, orgname, address, phoneNumber, email, dorr);
                                /*if (username.getText().toString().equals("admin") && (password.getText().toString().equals("admin"))) {
                                    startActivity(new Intent(getApplicationContext(), AdminNav.class));
                                } else {*/
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                //}

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