package utar.edu.my.individualassignment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ReadyActivity extends AppCompatActivity {

    public static final String EXTRA_TARGET = "target_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        Button btnStart = findViewById(R.id.btnStart);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        //use for extra nav for compare & compose
        btnStart.setOnClickListener(v -> {
            String target = getIntent().getStringExtra(EXTRA_TARGET);
            if (target == null) return;

            switch (target) {
                case "compare":
                    startActivity(new Intent(this, CompareActivity.class));
                    break;
                case "compose":
                    startActivity(new Intent(this, ComposeActivity.class));
                    break;
            }

            finish();
        });
    }
}
