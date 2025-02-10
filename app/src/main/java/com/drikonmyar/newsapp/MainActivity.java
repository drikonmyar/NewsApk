package com.drikonmyar.newsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 newsViewPager;
    private NewsAdapter newsAdapter;
    private List<String> newsTexts = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsViewPager = findViewById(R.id.newsViewPager);

        // Set orientation to vertical
        newsViewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        // Fetch data from Firebase Realtime Database
        fetchDataFromFirebase();

        // Set custom page transformer for pro-level book-like animation
        newsViewPager.setPageTransformer(new ProVerticalBookPageTransformer());

        // Subscribe to Firebase Messaging
        FirebaseMessaging.getInstance().subscribeToTopic("TestMessage")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Successfully subscribed to TestMessage topic");
                    } else {
                        Log.e("Firebase", "Subscription to TestMessage failed", task.getException());
                    }
                });

        // Messaging Token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("TokenDetails", "Token failed to receive!!");
                return;
            }
            String token = task.getResult();
            Log.d("TOKEN", token);
        });
    }

    private void fetchDataFromFirebase() {
        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("news");

        // Fetch data from the "news" node
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear previous data
                newsTexts.clear();
                imageUrls.clear();

                // Iterate through each news item
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String text = snapshot.child("text").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    // Add values to the lists
                    if (text != null && imageUrl != null) {
                        newsTexts.add(text);
                        imageUrls.add(imageUrl);
                    }
                }

                // Set the data to the adapter
                newsAdapter = new NewsAdapter(newsTexts.toArray(new String[0]), imageUrls.toArray(new String[0]));
                newsViewPager.setAdapter(newsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }

    private class ProVerticalBookPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f; // Minimum scale for pages
        private static final float MIN_ALPHA = 0.5f; // Minimum alpha for pages
        private static final float MAX_ROTATION = 90f; // Maximum rotation angle for 3D effect

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageHeight = page.getHeight();
            int pageWidth = page.getWidth();

            if (position < -1) { // [-Infinity, -1)
                // Page is way off-screen to the top
                page.setAlpha(0);
            } else if (position <= 1) { // [-1, 1]
                // Apply transformations for pages in view
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float verticalMargin = pageHeight * (1 - scaleFactor) / 2;
                float horizontalMargin = pageWidth * (1 - scaleFactor) / 2;

                if (position < 0) {
                    // Page is moving to the top or is at rest
                    page.setTranslationY(verticalMargin - horizontalMargin / 2);
                } else {
                    // Page is moving to the bottom
                    page.setTranslationY(-verticalMargin + horizontalMargin / 2);
                }

                // Apply scaling
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Apply alpha (fade effect)
                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

                // Apply 3D rotation for a book-like effect
                float rotation = -MAX_ROTATION * position; // Rotate along the X-axis
                page.setRotationX(rotation);

                // Add depth effect using translationZ
                page.setTranslationZ(position < 0 ? -position * pageWidth : 0);
            } else { // (1, +Infinity]
                // Page is way off-screen to the bottom
                page.setAlpha(0);
            }
        }
    }
}