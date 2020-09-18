package com.dev.login_example;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
FirebaseAuth firebaseAuth;
CircleImageView circleImageView;
EditText otp,number,name;
Uri uri=null;
    Spinner spinner;
TextView gen;
private static final int PICK_IMAGE_REQUEST = 1;
StorageReference firebaseStorage;
CollectionReference firestore;
FirebaseFirestore users;
DatePicker datePicker;
    List<Gender> genderList=new ArrayList<>();
Button getOtp,getVerify;
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            Intent intent=new Intent(MainActivity.this,Profile.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gen=findViewById(R.id.gendeSelect);
        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();
        datePicker=findViewById(R.id.date);
        name=findViewById(R.id.name);
        circleImageView=findViewById(R.id.cir_login);
        number=findViewById(R.id.number);
        spinner=findViewById(R.id.genderdecide);
        getOtp=findViewById(R.id.numbutton);
        firestore=FirebaseFirestore.getInstance().collection("gender");
        users=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance().getReference("uploads");
        firestore.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null)return;
                List<Gender> temp=new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot:value){
                    Gender gender=documentSnapshot.toObject(Gender.class);
//                    genderList.add(gender);
                    genderList.add(new Gender(gender.getName()));
                }

            }
        });
//        genderList.add(new Gender("ok"));
        genderList.add(new Gender("Others"));
        ArrayAdapter<Gender> adapter=new ArrayAdapter<Gender>(this,android.R.layout.simple_spinner_item,genderList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        otp=findViewById(R.id.otp);
        getVerify=findViewById(R.id.otpbutton);
        final String[] code = new String[1];
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        });
//        spinner.set
        getVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri==null || name.getText().toString().isEmpty() || otp.getText().toString().isEmpty() || spinner.getSelectedItem()==null){
                    Toast.makeText(MainActivity.this,"Upload all data",Toast.LENGTH_LONG).show();
                    return;
                }
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(code[0],otp.getText().toString());
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Bitmap bitmap= null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos=new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                            byte[] dat=baos.toByteArray();
                            final StorageReference ref = firebaseStorage.child(firebaseAuth.getUid()+".jpeg");
                            final UploadTask uploadTask = ref.putBytes(dat);
                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return ref.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
//                                        users
                                        CollectionReference col=users.collection(firebaseAuth.getUid());
                                        col.add(new USER(name.getText().toString(),number.getText().toString(),spinner.getSelectedItem().toString(),downloadUri.toString(),String.valueOf(datePicker.getDayOfMonth())+" "+String.valueOf(datePicker.getMonth())+" "+String.valueOf(datePicker.getYear()))).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show();
                                                Intent intent=new Intent(MainActivity.this,Profile.class);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        // Handle failures
                                        // ...
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number.getText().toString().trim().isEmpty()){
                    Toast.makeText(MainActivity.this,"Enter Mobile Number",Toast.LENGTH_LONG).show();
                    return;
                }
                PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallBacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        code[0] =s;
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                };
                verifyPhone(number.getText().toString(),mcallBacks);
            }
        });

    }
    public void verifyPhone(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbac
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uri=data.getData();
            Glide.with(this).load(uri).into(circleImageView);

        }
    }
    List<Gender> swap(List<Gender> g2){

        return g2;
    }
}