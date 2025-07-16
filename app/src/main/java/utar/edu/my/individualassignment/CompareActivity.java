package utar.edu.my.individualassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.Random;

public class CompareActivity extends AppCompatActivity {

    private TextView txtNum1, txtNum2, txtQuestion, txtTimer;
    private Button btnBig, btnSmall;
    private int num1, num2;
    private CountDownTimer countDownTimer;
    private final long totalTime = 15000;
    private final long interval = 1000;
    private boolean answered = false;

    private MediaPlayer backgroundMusic;

    private MediaPlayer correctSound, wrongSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        txtNum1 = findViewById(R.id.txtNum1);
        txtNum2 = findViewById(R.id.txtNum2);
        txtQuestion = findViewById(R.id.txtQuestion);
        txtTimer = findViewById(R.id.txtTimer);
        btnBig = findViewById(R.id.btnBig);
        btnSmall = findViewById(R.id.btnSmall);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnBig.setOnClickListener(v -> {
            if (!answered) {
                answered = true;
                checkAnswer(true);
                stopTimer();
            }
        });

        btnSmall.setOnClickListener(v -> {
            if (!answered) {
                answered = true;
                checkAnswer(false);
                stopTimer();
            }
        });

        generateNumbers();


        // Bg Music
        backgroundMusic = MediaPlayer.create(this, R.raw.funcbgsound);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();

        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.wrong);
    }

    private void generateNumbers() {
        Random rand = new Random();
        num1 = rand.nextInt(100) + 1;
        num2 = rand.nextInt(100) + 1; // ori 0-99 now 1->100

        while (num1 == num2) {
            num2 = rand.nextInt(100) + 1;
        }

        txtNum1.setText(String.valueOf(num1));
        txtNum2.setText(String.valueOf(num2));
        txtQuestion.setText("Is " + num1 + " bigger or smaller than " + num2 + "?");

        answered = false;
        btnBig.setEnabled(true);
        btnSmall.setEnabled(true);
        startTimer();
    }

    private void checkAnswer(boolean choseBigger) {
        boolean isCorrect = choseBigger ? num1 > num2 : num1 < num2;
        showResultDialog(isCorrect, false);
    }

    private void showResultDialog(boolean isCorrect, boolean isTimeout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (isTimeout) {
            builder.setTitle("⏰ Time’s Up!");
            builder.setMessage("You didn’t answer in time.\n\nCorrect answer: " + (num1 > num2 ? "Bigger" : "Smaller"));
            if (wrongSound != null) wrongSound.start();
        } else if (isCorrect) {
            builder.setTitle("✅ Correct!");
            builder.setMessage("Great job! You picked the correct answer.\n\n" + num1 + (num1 > num2 ? " > " : " < ") + num2);
            if (correctSound != null) correctSound.start();
        } else {
            builder.setTitle("❌ Wrong!");
            builder.setMessage("Oops! That was incorrect.\n\nCorrect answer: " + (num1 > num2 ? "Bigger" : "Smaller"));
            if (wrongSound != null) wrongSound.start();
        }

        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> generateNumbers());
        builder.show();
    }


    private void startTimer() {
        txtTimer.setText("⏳: 15s");

        countDownTimer = new CountDownTimer(totalTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("⏳ " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                if (!answered) {
                    answered = true;
                    btnBig.setEnabled(false);
                    btnSmall.setEnabled(false);
                    showResultDialog(false, true);
                }
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();

        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }

        if (correctSound != null) {
            correctSound.release();
            correctSound = null;
        }
        if (wrongSound != null) {
            wrongSound.release();
            wrongSound = null;
        }
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

}
