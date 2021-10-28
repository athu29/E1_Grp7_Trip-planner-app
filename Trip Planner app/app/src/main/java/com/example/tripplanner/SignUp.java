package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mail,name,password,password2,age,phone;
    private ProgressBar progress;


    private Button imageButton;
    private TextView imageStatus;
    private String imageName;
    private String imageURL;

    private StorageReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mail = (EditText) findViewById(R.id.mailText);
        name = (EditText) findViewById(R.id.nameText);
        phone = (EditText) findViewById(R.id.phoneText);
        age = (EditText) findViewById(R.id.ageText);
        password = (EditText) findViewById(R.id.passwordText);
        password2 = (EditText) findViewById(R.id.passwordText2);

        imageStatus = (TextView) findViewById(R.id.uploadImageStatus);

        progress = (ProgressBar) findViewById(R.id.signUpProgressBar);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseStorage.getInstance().getReference();




    }

    public void toMainActivity(View view) {
        startActivity(new Intent(SignUp.this,MainActivity.class));
    }

    public void signUpUser(View view) {
        String mailText = mail.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String passwordText2 = password2.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        int ageText = Integer.parseInt(age.getText().toString().trim());
        String nameText = name.getText().toString().trim();

        if (nameText.isEmpty()){
            name.setError("Name is required");
            name.requestFocus();
            return;
        }
        if (passwordText.length() < 6 ){
            password.setError("Password Length less than 6 ");
            password.requestFocus();
            return;
        }
        if (phoneText.isEmpty()){
            phone.setError("Phone Number is required");
            phone.requestFocus();
            return;
        }
        if (!passwordText.equals(passwordText2)){
            password2.setError("Password Don't Match");
            password2.requestFocus();
            return;
        }
        if (ageText==0){
            age.setError("Age is required");
            name.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mailText).matches()){
            mail.setError("Enter Proper email address");
            mail.requestFocus();
            return;
        }
        if (imageURL.isEmpty()){
            Toast.makeText(SignUp.this, "Upload Image", Toast.LENGTH_LONG).show();

        }


        else {
            progress.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(mailText, passwordText)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i("hello",imageURL);
                                User user = new User(nameText,mailText,phoneText,ageText,imageURL);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUp.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(SignUp.this,MainActivity.class));
                                        } else {
                                            Toast.makeText(SignUp.this, "Failed to Register", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(SignUp.this, "Failed to Register", Toast.LENGTH_LONG).show();
                            }
                            progress.setVisibility(View.INVISIBLE);

                        }
                    });
        }




    }
    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(this.getContentResolver(), selectedImage);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imagedata = baos.toByteArray();

                imageName = UUID.randomUUID().toString().replace(" ","") + ".jpg";

                reference.child("images")
                        .child(imageName)
                        .putBytes(imagedata)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("hello",reference.child("images").toString());

                                Toast.makeText(SignUp.this, "Failed to Upload Image Try Again", Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.child("images").child(imageName).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageURL = uri.toString();
                                                Toast.makeText(SignUp.this, "Uploaded Image", Toast.LENGTH_LONG).show();
                                                imageStatus.setText("Uploaded");
                                                imageStatus.setTextColor(Color.rgb(23,200,22));
                                                Log.i("hello",imageURL);
                                            }
                                        });

                            }
                        });

            }

//                ImageView imageView = (ImageView) findViewById(R.id.imageView);
//                imageView.setImageBitmap(bitmap);
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


        public void uploadImage (View view){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    getPhoto();
                }
            }
        }
}
