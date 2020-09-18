package com.dev.login_example;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    CollectionReference firestore;
    CircleImageView imageUrl;
    TextView name,number,gender,dateOfBirth;
    USER user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth=FirebaseAuth.getInstance();
        name=findViewById(R.id.pName);
        number=findViewById(R.id.pNumber);
        gender=findViewById(R.id.pGender);
        imageUrl=findViewById(R.id.pImage);
        dateOfBirth=findViewById(R.id.dob);
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
//        Toast.makeText(this,firebaseUser.getUid(),Toast.LENGTH_LONG).show();
        firestore= FirebaseFirestore.getInstance().collection(firebaseUser.getUid());
//        Task<QuerySnapshot> user=firestore.get
        firestore.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null)return;
                for (QueryDocumentSnapshot documentSnapshot:value){
                    user=documentSnapshot.toObject(USER.class);
                }
                Glide.with(Profile.this).load(user.getImageUrl()).into(imageUrl);
                name.setText("Name: "+user.getName());
                number.setText("Number: "+user.getNumber());
                gender.setText("Gender: "+user.getGender());
                dateOfBirth.setText("D.O.B: "+user.getDateOfBirth());
            }

        });
    }
}