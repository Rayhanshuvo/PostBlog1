package database.google.com.postblog.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import database.google.com.postblog.R;

public class PostListActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseUser muser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        mAuth=FirebaseAuth.getInstance();
        muser=mAuth.getCurrentUser();

        mDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mDatabase.getReference().child("MBlog");
        mDatabaseReference.keepSynced(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_add:

                if(muser!=null && mAuth!=null){

                    startActivity(new Intent(PostListActivity.this,AddPostActivity.class));
                    finish();
                }
                break;

            case R.id.action_signout:

                if(muser!=null && mAuth!=null){
                    mAuth.signOut();
                    startActivity(new Intent(PostListActivity.this,MainActivity.class));
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
