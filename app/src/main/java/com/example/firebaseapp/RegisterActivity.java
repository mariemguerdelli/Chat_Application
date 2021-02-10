package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //widgets
    EditText userEt, passEt, emailEt,phoneEt;
    Button registerBtn;

    //Firebase

    FirebaseAuth auth;
    DatabaseReference myRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //Initializing widgets:
        userEt = findViewById(R.id.userEditText);
        passEt = findViewById(R.id.passEditText);
        emailEt = findViewById(R.id.emailEditText);
        phoneEt = findViewById(R.id.phoneEditText);
        registerBtn = findViewById(R.id.buttonRegister);


        //Firebase Auth
        auth = FirebaseAuth.getInstance();

        //Adding Event Listener to Button Register
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_text = userEt.getText().toString();
                String email_text = emailEt.getText().toString();
                String pass_text = passEt.getText().toString();
                String phone_text = phoneEt.getText().toString();

                if(TextUtils.isEmpty(username_text) || TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text) || TextUtils.isEmpty(phone_text)){
                    Toast.makeText(RegisterActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();

                }else{
                    RegisterNow(username_text,email_text,pass_text,phone_text);
                }
            }
        });

    }

    private void RegisterNow(final String username, String email, String password, final String phone){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);


                            //HashMaps
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");
                            hashMap.put("status", "offline");
                            hashMap.put("phone",phone);


                            //Opening the main activity after successful registration
                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }

                                }
                            });

                        }else {
                            Toast.makeText(RegisterActivity.this, "Invalid Email or Password",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
