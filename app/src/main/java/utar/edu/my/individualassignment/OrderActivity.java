package utar.edu.my.individualassignment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class OrderActivity extends AppCompatActivity {

    private TextView txtQuestion, txtTimer;
    private Button btnOptionA, btnOptionB, btnOptionC, btnOptionD;
    private Button btnAscLevel, btnDescLevel;

    private String correctAnswer;
    private boolean isAscending = true;
    private CountDownTimer countDownTimer;
    private final long TIME_LIMIT_MS = 20000;
    private boolean answered = false;

    private String optionA, optionB, optionC, optionD;
    private String correctOptionLabel;

    private MediaPlayer backgroundMusic;

    private MediaPlayer correctSound, wrongSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        txtQuestion = findViewById(R.id.txtLevel);

        txtTimer = findViewById(R.id.txtTimer);
        btnAscLevel = findViewById(R.id.btnAscLevel);
        btnDescLevel = findViewById(R.id.btnDescLevel);
        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        setOptionsVisibility(View.GONE);
        txtTimer.setVisibility(View.GONE);

        btnAscLevel.setOnClickListener(v -> {
            isAscending = true;
            btnAscLevel.setVisibility(View.GONE);
            btnDescLevel.setVisibility(View.GONE);
            generateQuestion();
        });

        btnDescLevel.setOnClickListener(v -> {
            isAscending = false;
            btnAscLevel.setVisibility(View.GONE);
            btnDescLevel.setVisibility(View.GONE);
            generateQuestion();
        });

        View.OnClickListener answerClickListener = v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            if (answered) return;

            answered = true;
            String selectedAnswer = "";

            if (v == btnOptionA) selectedAnswer = optionA;
            else if (v == btnOptionB) selectedAnswer = optionB;
            else if (v == btnOptionC) selectedAnswer = optionC;
            else if (v == btnOptionD) selectedAnswer = optionD;

            showResultDialog(selectedAnswer.equals(correctAnswer), false);
        };

        btnOptionA.setOnClickListener(answerClickListener);
        btnOptionB.setOnClickListener(answerClickListener);
        btnOptionC.setOnClickListener(answerClickListener);
        btnOptionD.setOnClickListener(answerClickListener);

        btnBack.setOnClickListener(v -> finish());

        // Bg Music
        backgroundMusic = MediaPlayer.create(this, R.raw.funcbgsound);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();

        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.wrong);
    }

    private void generateQuestion() {
        ArrayList<Integer> originalNumbers = generateUniqueRandomNumbers(4, 1, 99);
        String orderText = isAscending ? "ascending" : "descending";
        txtQuestion.setText("Which is the correct " + orderText + " order for: " + originalNumbers.toString().replace("[", "").replace("]", ""));

        ArrayList<Integer> correctList = new ArrayList<>(originalNumbers);
        if (isAscending) {
            Collections.sort(correctList);
        } else {
            correctList.sort(Collections.reverseOrder());
        }
        correctAnswer = listToString(correctList); // correct "_" string

        ArrayList<String> options = new ArrayList<>();
        options.add(correctAnswer); //correct option add to option

        //not duplication 3 num generate
        while (options.size() < 4) {
            ArrayList<Integer> temp = new ArrayList<>(originalNumbers);
            Collections.shuffle(temp);
            String option = listToString(temp);
            if (!options.contains(option)) {
                options.add(option);
            }
        }

        Collections.shuffle(options); //random display
        optionA = options.get(0);
        optionB = options.get(1);
        optionC = options.get(2);
        optionD = options.get(3);

        btnOptionA.setText("A. " + optionA);
        btnOptionB.setText("B. " + optionB);
        btnOptionC.setText("C. " + optionC);
        btnOptionD.setText("D. " + optionD);

        if (optionA.equals(correctAnswer)) correctOptionLabel = "A";
        else if (optionB.equals(correctAnswer)) correctOptionLabel = "B";
        else if (optionC.equals(correctAnswer)) correctOptionLabel = "C";
        else if (optionD.equals(correctAnswer)) correctOptionLabel = "D";

        answered = false;
        setOptionsVisibility(View.VISIBLE);
        txtTimer.setVisibility(View.VISIBLE);

        startTimer();
    }

    private void startTimer() {
        txtTimer.setText("⏳ 15s");
        countDownTimer = new CountDownTimer(TIME_LIMIT_MS, 1000) {
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("⏳ " + (millisUntilFinished / 1000) + "s");
            }

            public void onFinish() {
                if (!answered) {
                    answered = true;
                    showResultDialog(false, true);
                }
            }
        };
        countDownTimer.start();
    }

    private void showResultDialog(boolean isCorrect, boolean isTimeout) {
        String title, message;

        if (isTimeout) {
            title = "⏰ Time’s Up!";
            message = "You didn’t answer in time.\n\nCorrect answer: " + correctOptionLabel + ". " + correctAnswer;
            if (wrongSound != null) wrongSound.start();
        } else if (isCorrect) {
            title = "🎉 Correct!";
            message = "Well done! That's the correct order.\n\nAnswer: " + correctOptionLabel + ". " + correctAnswer;
            if (correctSound != null) correctSound.start();
        } else {
            title = "❌ Wrong!";
            message = "Oops! That was not correct.\n\nCorrect answer: " + correctOptionLabel + ". " + correctAnswer;
            if (wrongSound != null) wrongSound.start();
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    btnAscLevel.setVisibility(View.VISIBLE);
                    btnDescLevel.setVisibility(View.VISIBLE);
                    txtQuestion.setText("Choose an Order to begin!!");
                    txtTimer.setVisibility(View.GONE);
                    clearOptions();
                    setOptionsVisibility(View.GONE);
                })
                .show();
    }

    private ArrayList<Integer> generateUniqueRandomNumbers(int count, int min, int max) {
        Random rand = new Random();
        ArrayList<Integer> list = new ArrayList<>();
        while (list.size() < count) {
            int num = rand.nextInt(max - min + 1) + min; //random num generate unique
            if (!list.contains(num)) list.add(num); //generate until count 4
        }
        return list;
    }

    private String listToString(ArrayList<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int num : list) sb.append(num).append(" "); //add num into list
        return sb.toString().trim(); //space end delete
    }

    private void setOptionsVisibility(int visibility) {
        btnOptionA.setVisibility(visibility);
        btnOptionB.setVisibility(visibility);
        btnOptionC.setVisibility(visibility);
        btnOptionD.setVisibility(visibility);
    }

    private void clearOptions() {
        btnOptionA.setText("");
        btnOptionB.setText("");
        btnOptionC.setText("");
        btnOptionD.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

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
