package utar.edu.my.individualassignment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ComposeActivity extends AppCompatActivity {

    private TextView targetNum, selectedSlot1, selectedSlot2, txtTimer;
    private LinearLayout numberChoices;
    private Button btnCheck;

    private int targetNumber;
    private final ArrayList<Button> choiceButtons = new ArrayList<>();
    private Integer slot1Value = null;
    private Integer slot2Value = null;
    private int correctFirst;
    private int correctSecond;
    private CountDownTimer countDownTimer;
    private final long TIME_LIMIT_MS = 25000;

    private boolean answered = false;

    private MediaPlayer backgroundMusic;

    private MediaPlayer correctSound, wrongSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        targetNum = findViewById(R.id.txtTargetNumber);
        selectedSlot1 = findViewById(R.id.txtSlot1);
        selectedSlot2 = findViewById(R.id.txtSlot2);
        txtTimer = findViewById(R.id.txtTimer);
        numberChoices = findViewById(R.id.numberChoices);
        btnCheck = findViewById(R.id.btnCheck);

        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> resetSelection());


        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnCheck.setOnClickListener(v -> {
            if (!answered) {
                answered = true;
                stopTimer();
                checkAnswer();
            }
        });

        btnBack.setOnClickListener(v -> finish());

        generateRound();

        // Bg Music
        backgroundMusic = MediaPlayer.create(this, R.raw.funcbgsound);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();

        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.wrong);
    }

    private void resetSelection() {
        slot1Value = null;
        slot2Value = null;
        selectedSlot1.setText("__");
        selectedSlot2.setText("__");
        btnCheck.setEnabled(false);

        for (Button button : choiceButtons) {
            button.setEnabled(true);
        }
    }

    private void generateRound() {
        Random rand = new Random();
        targetNumber = rand.nextInt(16) + 5;  // generate 5->20
        targetNum.setText("Target: " + targetNumber);

        slot1Value = null;
        slot2Value = null;
        answered = false;
        selectedSlot1.setText("__");
        selectedSlot2.setText("__");
        btnCheck.setEnabled(false);

        numberChoices.removeAllViews();
        choiceButtons.clear();
        txtTimer.setText("⏳ 15s");

        correctFirst = rand.nextInt(targetNumber - 1) + 1; // between 1 and targetNum -1
        correctSecond = targetNumber - correctFirst;

        ArrayList<Integer> choices = new ArrayList<>();
        if (correctFirst != correctSecond) { //correct pair unique
            choices.add(correctFirst);
            choices.add(correctSecond);
        } else {
            choices.add(correctFirst);
        }

        while (choices.size() < 4) { //not duplicated
            int num = rand.nextInt(targetNumber);
            if (num > 0 && !choices.contains(num)) {
                choices.add(num);
            }
        }

        Collections.shuffle(choices);

        for (int num : choices) { // 4 button
            Button numberButton = new Button(this);
            numberButton.setText(String.valueOf(num));
            numberButton.setTextSize(18);
            numberButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            numberButton.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_blue_light));
            numberButton.setOnClickListener(v -> selectNumber(num, numberButton));
            numberChoices.addView(numberButton);
            choiceButtons.add(numberButton);
        }

        startTimer();
    }

    private void selectNumber(int value, Button button) {
        if (slot1Value == null) {
            slot1Value = value;
            selectedSlot1.setText(String.valueOf(value));
            button.setEnabled(false);
        } else if (slot2Value == null) {
            slot2Value = value;
            selectedSlot2.setText(String.valueOf(value));
            button.setEnabled(false);
        }

        btnCheck.setEnabled(slot1Value != null && slot2Value != null);
    }

    private void checkAnswer() {
        int sum = slot1Value + slot2Value;
        boolean isCorrect = (sum == targetNumber);

        // Play the appropriate sound
        if (isCorrect && correctSound != null) {
            correctSound.start();
        } else if (!isCorrect && wrongSound != null) {
            wrongSound.start();
        }

        String title = isCorrect ? "🎉 Correct!" : "❌ Incorrect!";
        String message = isCorrect
                ? "You chose: " + slot1Value + " + " + slot2Value + " = " + sum
                : "Your answer: " + slot1Value + " + " + slot2Value + " = " + sum +
                "\n\nCorrect answer: " + correctFirst + " + " + correctSecond + " = " + targetNumber;

        showResultDialog(title, message);
    }


    private void showResultDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> generateRound())
                .show();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TIME_LIMIT_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("⏳ " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                if (!answered) {
                    answered = true;
                    btnCheck.setEnabled(false);
                    showResultDialog("⏰ Time's Up!", "You didn't answer in time.\n\nCorrect answer: " +
                            correctFirst + " + " + correctSecond + " = " + targetNumber);

                }
                if (wrongSound != null) wrongSound.start();
            }
        };
        countDownTimer.start();
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
