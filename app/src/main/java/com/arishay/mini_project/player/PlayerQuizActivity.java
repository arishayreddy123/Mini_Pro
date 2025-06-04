package com.arishay.mini_project.player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import com.arishay.mini_project.R;
import com.arishay.mini_project.model.Question;
import com.google.firebase.firestore.*;

import java.util.*;

public class PlayerQuizActivity extends AppCompatActivity {

    private TextView questionText, scoreText, feedbackText, correctNumbersText, incorrectNumbersText;
    private RadioGroup optionsGroup;
    private Button submitBtn, returnBtn;

    private FirebaseFirestore db;
    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String tournamentId;

    private List<Integer> correctIndexes = new ArrayList<>();
    private List<Integer> incorrectIndexes = new ArrayList<>();

    private boolean isWaiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_quiz);

        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        feedbackText = findViewById(R.id.feedbackText);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitBtn = findViewById(R.id.submitBtn);
        returnBtn = findViewById(R.id.returnBtn);
        correctNumbersText = findViewById(R.id.correctNumbersText);
        incorrectNumbersText = findViewById(R.id.incorrectNumbersText);

        tournamentId = getIntent().getStringExtra("tournamentId");
        db = FirebaseFirestore.getInstance();

        loadQuestions();

        submitBtn.setOnClickListener(v -> checkAnswer());
        returnBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, PlayerViewTournamentsActivity.class));
            finish();
        });
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
                    showNextQuestion();
                });
    }

    private void showNextQuestion() {
        if (currentQuestionIndex >= questionList.size()) {
            showScore();
            return;
        }

        optionsGroup.removeAllViews();
        feedbackText.setVisibility(View.GONE);

        Question q = questionList.get(currentQuestionIndex);
        questionText.setText("Q" + (currentQuestionIndex + 1) + ": " + q.question);

        List<String> allOptions = new ArrayList<>(q.incorrect_answers);
        allOptions.add(q.correct_answer);
        Collections.shuffle(allOptions);

        for (String option : allOptions) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            optionsGroup.addView(rb);
        }
    }

    private void checkAnswer() {
        if (isWaiting) return;

        int selectedId = optionsGroup.getCheckedRadioButtonId();

        // ❌ No answer selected
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Answer selected
        RadioButton selectedBtn = findViewById(selectedId);
        String selectedAnswer = selectedBtn.getText().toString();
        Question current = questionList.get(currentQuestionIndex);
        String correctAnswer = current.correct_answer;

        isWaiting = true;
        submitBtn.setEnabled(false);

        if (selectedAnswer.equals(correctAnswer)) {
            score++;
            correctIndexes.add(currentQuestionIndex + 1);
            feedbackText.setText("✅ Correct!");
        } else {
            incorrectIndexes.add(currentQuestionIndex + 1);
            feedbackText.setText("❌ Incorrect.\nCorrect answer: " + correctAnswer);
        }

        feedbackText.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            feedbackText.setVisibility(View.GONE);
            submitBtn.setEnabled(true);
            isWaiting = false;
            currentQuestionIndex++;

            if (currentQuestionIndex < questionList.size()) {
                showNextQuestion();
            } else {
                showScore();
            }
        }, 2500);
    }

    private void showScore() {
        submitBtn.setVisibility(View.GONE);
        returnBtn.setVisibility(View.VISIBLE);

        questionText.setText("🎉 Quiz Completed!");
        scoreText.setText("Your Score: " + score + "/" + questionList.size());
        optionsGroup.removeAllViews();

        correctNumbersText.setText("✅ Correct Questions: " + correctIndexes.toString());
        incorrectNumbersText.setText("❌ Incorrect Questions: " + incorrectIndexes.toString());
        correctNumbersText.setVisibility(View.VISIBLE);
        incorrectNumbersText.setVisibility(View.VISIBLE);
    }
}
