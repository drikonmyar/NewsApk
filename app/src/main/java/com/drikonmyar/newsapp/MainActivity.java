package com.drikonmyar.newsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 newsViewPager;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsViewPager = findViewById(R.id.newsViewPager);

        // Set orientation to vertical
        newsViewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        List<String> newsTexts = new ArrayList<>();
        newsTexts.add("A bot-driven car is an autonomous vehicle that uses AI, sensors, and GPS to navigate without human input. It detects obstacles, interprets traffic signals, and makes real-time decisions using advanced algorithms and deep learning. Companies like Tesla and Waymo are developing self-driving technology to enhance safety and efficiency, shaping the future.");
        newsTexts.add("A bot-created bot is an autonomous system that uses AI, algorithms, and data to develop new bots without human input. It analyzes patterns, optimizes code, and makes real-time improvements using machine learning and automation. Companies like OpenAI and DeepMind are advancing self-learning AI to enhance efficiency and innovation, shaping the future.");
        newsTexts.add("A bot coding is an autonomous system that uses AI, algorithms, and data to write code without human input. It analyzes patterns, debugs errors, and makes the real-time improvements using machine learning and automation. Companies like OpenAI and DeepMind are advancing AI-driven coding to enhance efficiency.");

        // Image URLs instead of resource IDs
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://file-examples.com/storage/fe21422a6d67aa28993b797/2017/10/file_example_JPG_100kB.jpg");
        imageUrls.add("https://file-examples.com/storage/fe21422a6d67aa28993b797/2017/10/file_example_JPG_100kB.jpg");
        imageUrls.add("https://file-examples.com/storage/fe21422a6d67aa28993b797/2017/10/file_example_JPG_100kB.jpg");

        newsAdapter = new NewsAdapter(newsTexts.toArray(new String[0]), imageUrls.toArray(new String[0]));
        newsViewPager.setAdapter(newsAdapter);

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
            if(!task.isSuccessful()){
                Log.e("TokenDetails", "Token failed to receive!!");
                return;
            }
            String token = task.getResult();
            Log.d("TOKEN", token);
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