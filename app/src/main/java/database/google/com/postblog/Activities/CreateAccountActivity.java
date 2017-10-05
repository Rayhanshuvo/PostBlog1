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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import database.google.com.postblog.R;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstname,lastname,email,password;
    Button createAccount;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private ProgressDialog mProgressDialog;
    private ImageButton profilepic;
    private Uri mImageUri=null;
    private  Uri resultUri=null;
    private final static int GALLERY_CODE=1;
    private StorageReference mFirebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mDatabase.getReference().child("MUsers");
        mFirebaseStorage=FirebaseStorage.getInstance().getReference().child("MBlog_Profile");

        mAuth=FirebaseAuth.getInstance();
        mProgressDialog=new ProgressDialog(this);
        firstname= (EditText) findViewById(R.id.editText5);
        lastname=(EditText)findViewById(R.id.editText6);
        email=(EditText)  findViewById(R.id.editText7);
        password=(EditText) findViewById(R.id.editText8);
        createAccount=(Button)findViewById(R.id.button5);
        profilepic=(ImageButton)findViewById(R.id.profilepic);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent galleryIntent=new Intent();
                //galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                //galleryIntent.setType("Images/*");
               // startActivityForResult(galleryIntent,GALLERY_CODE);


                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult( galleryIntent, GALLERY_CODE);
                //Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                //galleryIntent.setType("image/*");
               // startActivityForResult( galleryIntent, GALLERY_CODE);

            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){

            mImageUri=data.getData();
            //mPostImage.setImageURI(mImageUri);
            CropImage.activity(mImageUri).setAspectRatio(1,1)
                  .setGuidelines(CropImageView.Guidelines.ON)
                .start(CreateAccountActivity.this);
           // profilepic.setImageURI(mImageUri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                profilepic.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void createNewAccount() {

        final String name=firstname.getText().toString().trim();
        final String lname=lastname.getText().toString().trim();
        String em=email.getText().toString().trim();
        String pwd= password.getText().toString().trim();

        if(! TextUtils.isEmpty(name) && !TextUtils.isEmpty(lname)&& !TextUtils.isEmpty(em)&& !TextUtils.isEmpty(pwd)){

            mProgressDialog.setMessage("Creating Account......");
            mProgressDialog.show();


            mAuth.createUserWithEmailAndPassword(em,pwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    if(authResult!=null){
                        StorageReference imagepath=mFirebaseStorage.child( "MBlog_Profile").child(resultUri.getLastPathSegment());
                        imagepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                                   String userid=mAuth.getCurrentUser().getUid();
                                 DatabaseReference currentUserDb=mDatabaseReference.child(userid);
                                currentUserDb.child("firstname").setValue(name);
                                currentUserDb.child("lastname").setValue(lname);
                                currentUserDb.child("image").setValue(resultUri.toString());
                             //   currentUserDb.child("somrthingnew").setValue("hello");

                                mProgressDialog.dismiss();
                                //send users to postList
                                 Intent intent= new Intent(CreateAccountActivity.this,PostListActivity.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                 startActivity(intent);
                            }
                        });



                    }
                }
            });
        }


    }

   // @Override
  //  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
      //  if(requestCode==GALLEY_CODE && resultCode == RESULT_OK){
        //    Uri mImageUri=data.getData();
          //  CropImage.activity(mImageUri)
            //        .setGuidelines(CropImageView.Guidelines.ON)
              //      .start(CreateAccountActivity.this);
        //}
    //}


}
