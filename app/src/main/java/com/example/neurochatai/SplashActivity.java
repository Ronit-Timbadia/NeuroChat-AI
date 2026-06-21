package com.example.neurochatai;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View glow = findViewById(R.id.glowCircle);
        TextView appName = findViewById(R.id.appName);
        TextView credit = findViewById(R.id.credit);

        // Glow fade + scale
        ObjectAnimator glowFade = ObjectAnimator.ofFloat(glow, "alpha", 0f, 1f);
        ObjectAnimator glowScaleX = ObjectAnimator.ofFloat(glow, "scaleX", 0.5f, 1.2f);
        ObjectAnimator glowScaleY = ObjectAnimator.ofFloat(glow, "scaleY", 0.5f, 1.2f);

        // App name animation
        ObjectAnimator nameFade = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f);
        ObjectAnimator nameScaleX = ObjectAnimator.ofFloat(appName, "scaleX", 0.8f, 1f);
        ObjectAnimator nameScaleY = ObjectAnimator.ofFloat(appName, "scaleY", 0.8f, 1f);

        // Credit animation
        ObjectAnimator creditFade = ObjectAnimator.ofFloat(credit, "alpha", 0f, 1f);
        ObjectAnimator creditTranslate = ObjectAnimator.ofFloat(credit, "translationY", 50f, 0f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(glowFade, glowScaleX, glowScaleY);
        set.setDuration(1500);
        set.setInterpolator(new AccelerateDecelerateInterpolator());

        set.start();

        appName.postDelayed(() -> {
            AnimatorSet nameSet = new AnimatorSet();
            nameSet.playTogether(nameFade, nameScaleX, nameScaleY);
            nameSet.setDuration(1000);
            nameSet.start();
        }, 800);

        appName.postDelayed(() -> {
            AnimatorSet creditSet = new AnimatorSet();
            creditSet.playTogether(creditFade, creditTranslate);
            creditSet.setDuration(1000);
            creditSet.start();
        }, 1500);

        // Move to main screen
        appName.postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 3500);
    }
}