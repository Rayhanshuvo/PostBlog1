package database.google.com.postblog.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import database.google.com.postblog.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Button login;
    Button register;
    EditText email,password;
   FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        login=(Button) findViewById(R.id.button2);
        register=(Button)findViewById(R.id.button3);
        email=(EditText) findViewById(R.id.editText);
        password=(EditText) findViewById(R.id.editText2);

        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mUser=firebaseAuth.getCurrentUser();
                if(mUser!=null){
                    Toast.makeText(MainActivity.this,"Signed In",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,PostListActivity.class));
                    finish();

                }

                else{
                    Toast.makeText(MainActivity.this,"Not Signed In",Toast.LENGTH_SHORT).show();

                }
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(email.getText().toString())&&!TextUtils.isEmpty(password.getText().toString())){

                    String emaill,pwd;
                    emaill=email.getText().toString();
                    pwd=password.getText().toString();
                    loginInDatabase(emaill,pwd);

                }

                else{


                }
            }
        });
    }

    private void loginInDatabase(String emaill, String pwd) {

        mAuth.signInWithEmailAndPassword(emaill,pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(MainActivity.this,"You are signedI",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this,PostListActivity.class);
                    startActivity(intent);
                }

                else{

                    Toast.makeText(MainActivity.this,"There is a problem",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_signout){

            mAuth.signOut();


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null){
mAuth.removeAuthStateListener(mAuthListener);

        }
    }
}
