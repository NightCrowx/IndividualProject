package utar.edu.my.individualassignment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private MediaPlayer backgroundMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        NavigationView navView = findViewById(R.id.navigation_view);

        //drawer open/close
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // drawer nav
        navView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            int id = item.getItemId();

            if (id == R.id.nav_compare) {
                Intent intent = new Intent(this, ReadyActivity.class);
                intent.putExtra(ReadyActivity.EXTRA_TARGET, "compare");
                startActivity(intent);
            } else if (id == R.id.nav_order) {
                startActivity(new Intent(this, OrderActivity.class)); // no ReadyActivity
            } else if (id == R.id.nav_compose) {
                Intent intent = new Intent(this, ReadyActivity.class);
                intent.putExtra(ReadyActivity.EXTRA_TARGET, "compose");
                startActivity(intent);
            } else if (id == R.id.nav_exit) {
                finishAffinity();
            }

            return true;
        });

        // Main btn
        findViewById(R.id.btnCompare).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReadyActivity.class);
            intent.putExtra(ReadyActivity.EXTRA_TARGET, "compare");
            startActivity(intent);
        });

        findViewById(R.id.btnOrder).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OrderActivity.class));
        });

        findViewById(R.id.btnCompose).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReadyActivity.class);
            intent.putExtra(ReadyActivity.EXTRA_TARGET, "compose");
            startActivity(intent);
        });

        //bg music
        backgroundMusic = MediaPlayer.create(this, R.raw.bgsound);
        backgroundMusic.setLooping(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
}