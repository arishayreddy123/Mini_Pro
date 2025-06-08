package com.arishay.mini_project.player;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.arishay.mini_project.R;
import com.arishay.mini_project.model.Question;
import com.google.firebase.firestore.*;

import org.json.JSONObject;

import java.util.*;

public class PlayerQuizActivity extends AppCompatActivity {

    private TextView questionText, feedbackText, progressText;
    private RadioGroup optionsGroup;
    private Button submitBtn;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private List<Question> questionList = new ArrayList<>();
    private Map<Integer, String> selectedAnswers = new HashMap<>();

    private int currentQuestionIndex = 0;
    private String tournamentId;
    private boolean isWaiting = false;
    private boolean backPressedOnce = false;
    private Handler backHandler = new Handler();

    private static final String PREFS_NAME = "QuizPrefs";
    private static final String KEY_TOURNAMENT = "currentTournamentId";
    private static final String KEY_INDEX = "currentQuestionIndex";
    private static final String KEY_ANSWERS = "selectedAnswers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_quiz);

        questionText = findViewById(R.id.questionText);
        feedbackText = findViewById(R.id.feedbackText);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitBtn = findViewById(R.id.submitBtn);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.contains(KEY_TOURNAMENT)) {
            tournamentId = prefs.getString(KEY_TOURNAMENT, null);
            currentQuestionIndex = prefs.getInt(KEY_INDEX, 0);
            try {
                String answersJson = prefs.getString(KEY_ANSWERS, "{}");
                JSONObject json = new JSONObject(answersJson);
                for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                    String key = it.next();
                    selectedAnswers.put(Integer.parseInt(key), json.getString(key));
                }
                Toast.makeText(this, "Resumed previous quiz", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tournamentId = getIntent().getStringExtra("tournamentId");
        }

        db = FirebaseFirestore.getInstance();
        loadQuestions();

        submitBtn.setOnClickListener(v -> checkAnswer());
    }

    private void loadQuestions() {
        db.collection("tournaments")
                .document(tournamentId)
                .collection("questions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Question q = doc.toObject(Question.class);
                        questionList.add(q);
                    }
                    showQuestion();
                });
    }

    private void showQuestion() {
        feedbackText.setVisibility(TextView.GONE);
        optionsGroup.removeAllViews();

        Question q = questionList.get(currentQuestionIndex);
        questionText.setText("Q" + (currentQuestionIndex + 1) + ": " + q.question);

        progressText.setText("Question " + (currentQuestionIndex + 1) + " of " + questionList.size());
        progressBar.setMax(questionList.size());
        progressBar.setProgress(currentQuestionIndex + 1);

        List<String> allOptions = new ArrayList<>(q.incorrect_answers);
        allOptions.add(q.correct_answer);
        Collections.shuffle(allOptions);

        for (String option : allOptions) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            optionsGroup.addView(rb);

            String previouslySelected = selectedAnswers.get(currentQuestionIndex);
            if (previouslySelected != null && previouslySelected.equals(option)) {
                rb.setChecked(true);
            }
        }
    }

    private void checkAnswer() {
        if (isWaiting) return;

        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedBtn = findViewById(selectedId);
        String selectedAnswer = selectedBtn.getText().toString();

        Question current = questionList.get(currentQuestionIndex);
        String correctAnswer = current.correct_answer;

        selectedAnswers.put(currentQuestionIndex, selectedAnswer);

        isWaiting = true;
        submitBtn.setEnabled(false);

        if (selectedAnswer.equals(correctAnswer)) {
            feedbackText.setText("✅ Correct!");
        } else {
            feedbackText.setText("❌ Incorrect.\nCorrect answer: " + correctAnswer);
        }

        feedbackText.setVisibility(TextView.VISIBLE);

        new Handler().postDelayed(() -> {
            feedbackText.setVisibility(TextView.GONE);
            submitBtn.setEnabled(true);
            isWaiting = false;

            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                showQuestion();
            } else {
                // Clear resume cache
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

                showScoreDialog();
            }
        }, 2500);
    }

    private void showScoreDialog() {
        int correctCount = 0;
        for (int i = 0; i < questionList.size(); i++) {
            String selected = selectedAnswers.get(i);
            if (selected != null && selected.equals(questionList.get(i).correct_answer)) {
                correctCount++;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Quiz Completed")
                .setMessage("Thanks for playing!\nYour score: " + correctCount + "/" + questionList.size())
                .setPositiveButton("Back to Quizzes", (dialog, which) -> {
                    startActivity(new Intent(this, PlayerViewTournamentsActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(KEY_TOURNAMENT, tournamentId);
        editor.putInt(KEY_INDEX, currentQuestionIndex);

        JSONObject json = new JSONObject();
        for (Map.Entry<Integer, String> entry : selectedAnswers.entrySet()) {
            try {
                json.put(String.valueOf(entry.getKey()), entry.getValue());
            } catch (Exception ignored) {}
        }
        editor.putString(KEY_ANSWERS, json.toString());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (backPressedOnce) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit Quiz")
                    .setMessage("Are you sure you want to exit the quiz?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        startActivity(new Intent(this, PlayerViewTournamentsActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            backPressedOnce = true;
            Toast.makeText(this, "Press again to exit the quiz", Toast.LENGTH_SHORT).show();
            backHandler.postDelayed(() -> backPressedOnce = false, 2000);
        }
    }
}
