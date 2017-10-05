package database.google.com.postblog.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import database.google.com.postblog.Model.Blog;
import database.google.com.postblog.R;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;
    private DatabaseReference mPostDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
   private ProgressDialog mProgress;
    private static final int GALLERY_CODE=1;
    private Uri mImageUri=null;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgress=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mStorage= FirebaseStorage.getInstance().getReference();
        mPostDatabase= FirebaseDatabase.getInstance().getReference().child("MBlog");

        mPostImage=(ImageButton)findViewById(R.id.imageButton);
        mPostTitle=(EditText)findViewById(R.id.editText3);
        mPostDesc=(EditText)findViewById(R.id.editText4);
        mSubmitButton=(Button)findViewById(R.id.button4);

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult( galleryIntent, GALLERY_CODE);

            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Posting to our databaze
startPosting();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){

            mImageUri=data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

    private void startPosting() {

        mProgress.setMessage("Posting to Blog");
        mProgress.show();

        final String titleVal=mPostTitle.getText().toString().trim();
        final String descVal=mPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(titleVal)&& !TextUtils.isEmpty(descVal)){


            StorageReference filepath=mStorage.child("MBlog_Image").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadurl=taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost=mPostDatabase.push();
                    Map<String,String> dataToSave= new HashMap<>();
                    dataToSave.put("title",titleVal);
                    dataToSave.put("desc",descVal);
                    dataToSave.put("image",downloadurl.toString());
                    dataToSave.put("timestamp",String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userid",mUser.getUid());

                    newPost.setValue(dataToSave);
                    mProgress.dismiss();
                    //go
                    startActivity(new Intent(AddPostActivity.this,PostListActivity.class));
                    finish();

                }
            });

        }
    }
}
