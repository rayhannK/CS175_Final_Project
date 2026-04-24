package com.example.finalprojectgroup15;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectgroup15.databinding.ActivityFeedbackBinding;

public class FeedbackActivity extends AppCompatActivity {

    private ActivityFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.feedback_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.feedbackInput.setText(getString(R.string.feedback_prefill));
        binding.sendFeedbackButton.setOnClickListener(v -> sendFeedbackEmail());
    }

    private void sendFeedbackEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + getString(R.string.feedback_email)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, binding.feedbackInput.getText().toString().trim());

        if (emailIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, R.string.toast_email_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
