package technovations.ajuj.technovations2017;

/**
 * Created by jenny on 3/12/2017.
 */

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import technovations.ajuj.technovations2017.Login;
import technovations.ajuj.technovations2017.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText username, password, first_name, last_name, email, org_name, address, phoneNumber;
    private TextView guideline;
    private Button register, back;
    private CheckBox donor, receiver;
    private RequestQueue requestQueue;
    private StringRequest request;
    private String DorR;
    private String URL = "";
    String Img;
    private static int RESULT_LOAD_IMAGE = 1;
    private String propic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_register);

        Typeface face2 = Typeface.createFromAsset(getAssets(), "c_gothic.ttf");
        username = (EditText) findViewById(R.id.username_register);
        username.setTypeface(face2);
        password = (EditText) findViewById(R.id.password_register);
        password.setTypeface(face2);
        first_name = (EditText) findViewById(R.id.first_name_register);
        first_name.setTypeface(face2);
        last_name = (EditText) findViewById(R.id.last_name_register);
        last_name.setTypeface(face2);
        org_name = (EditText) findViewById(R.id.org_name_register);
        org_name.setTypeface(face2);
        email = (EditText) findViewById(R.id.email_register);
        email.setTypeface(face2);
        address = (EditText) findViewById(R.id.address_register);
        address.setTypeface(face2);
        phoneNumber = (EditText) findViewById(R.id.phone_number_register);
        phoneNumber.setTypeface(face2);
        donor = (CheckBox) findViewById(R.id.donor_register);
        donor.setTypeface(face2);
        receiver = (CheckBox) findViewById(R.id.receiver_register);
        receiver.setTypeface(face2);


        register = (Button) findViewById(R.id.register_register);
        register.setTypeface(face2);
        back = (Button) findViewById(R.id.back_register);
        back.setTypeface(face2);
        guideline = (TextView) findViewById(R.id.guideline);
        guideline.setTypeface(face2);

        requestQueue = Volley.newRequestQueue(this);

        register.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                request = new StringRequest(Request.Method.POST, "https://2017ajuj.000webhostapp.com/register.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                        try{
                            Toast.makeText(getApplicationContext(), "successfully inside the try", Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
                            if(jsonObject.has("success")){
                                Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),Login.class));
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
                        hashMap.put("username",username.getText().toString());
                        hashMap.put("password",password.getText().toString());
                        hashMap.put("first",first_name.getText().toString());
                        hashMap.put("last",last_name.getText().toString());
                        hashMap.put("email",email.getText().toString());
                        hashMap.put("orgname", org_name.getText().toString());
                        hashMap.put("address", address.getText().toString());
                        hashMap.put("phoneNumber", phoneNumber.getText().toString());
                        hashMap.put("dorr", DorR);
                        hashMap.put("propic",propic);

                        return hashMap;
                    }

                };

                requestQueue.add(request);
            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });



    }

    public void selectPropic(View v){
        propic = v.getTag().toString();
    }

    public void selectDorR(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.donor_register:
                if (checked) {
                    DorR = "donor";
                } else {
                    DorR = "receiver";
                }
                break;
            case R.id.receiver_register:
                if (checked) {
                    DorR = "receiver";
                } else {
                    DorR = "donor";
                }
                break;
        }
    }

}