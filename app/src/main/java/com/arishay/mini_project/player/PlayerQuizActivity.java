package com.arishay.mini_project.player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.arishay.mini_project.R;
import com.arishay.mini_project.model.Question;
import com.google.firebase.firestore.*;

import java.util.*;

public class PlayerQuizActivity extends AppCompatActivity {

    private TextView questionText, scoreText;
    private RadioGroup optionsGroup;
    private Button submitBtn, returnBtn;

    private FirebaseFirestore db;
    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String tournamentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_quiz);

        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitBtn = findViewById(R.id.submitBtn);
        returnBtn = findViewById(R.id.returnBtn);

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
        Question q = questionList.get(currentQuestionIndex);
        questionText.setText(q.question);

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
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedBtn = findViewById(selectedId);
        String selectedAnswer = selectedBtn.getText().toString();
        String correctAnswer = questionList.get(currentQuestionIndex).correct_answer;

        if (selectedAnswer.equals(correctAnswer)) {
            Toast.makeText(this, "✅ Correct!", Toast.LENGTH_SHORT).show();
            score++;
        } else {
            Toast.makeText(this, "❌ Incorrect. Answer: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }

        currentQuestionIndex++;
        showNextQuestion();
    }

    private void showScore() {
        submitBtn.setEnabled(false);
        questionText.setText("🎉 Quiz Completed!");
        scoreText.setText("Your Score: " + score + "/" + questionList.size());
        optionsGroup.removeAllViews();
        returnBtn.setVisibility(View.VISIBLE);
    }
}
