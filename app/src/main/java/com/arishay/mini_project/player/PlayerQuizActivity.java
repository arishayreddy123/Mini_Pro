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

    private TextView questionText, feedbackText, progressText;
    private RadioGroup optionsGroup;
    private Button submitBtn;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private List<Question> questionList = new ArrayList<>();
    private Map<Integer, String> selectedAnswers = new HashMap<>();

    private int currentQuestionIndex = 0;
    private String tournamentId;
    private boolean isEditing = false;
    private boolean isWaiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_quiz);

        // UI References
        questionText = findViewById(R.id.questionText);
        feedbackText = findViewById(R.id.feedbackText);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitBtn = findViewById(R.id.submitBtn);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        // Receive data
        tournamentId = getIntent().getStringExtra("tournamentId");
        isEditing = getIntent().getBooleanExtra("isEditing", false);
        currentQuestionIndex = getIntent().getIntExtra("editIndex", 0);

        if (getIntent().getSerializableExtra("answers") != null) {
            selectedAnswers = (Map<Integer, String>) getIntent().getSerializableExtra("answers");
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
        feedbackText.setVisibility(View.GONE);
        optionsGroup.removeAllViews();

        Question q = questionList.get(currentQuestionIndex);
        questionText.setText("Q" + (currentQuestionIndex + 1) + ": " + q.question);

        // Set progress
        progressText.setText("Question " + (currentQuestionIndex + 1) + " of " + questionList.size());
        progressBar.setMax(questionList.size());
        progressBar.setProgress(currentQuestionIndex + 1);

        // Display options
        List<String> allOptions = new ArrayList<>(q.incorrect_answers);
        allOptions.add(q.correct_answer);
        Collections.shuffle(allOptions);

        for (String option : allOptions) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            optionsGroup.addView(rb);

            // Pre-select if already answered
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

        feedbackText.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            feedbackText.setVisibility(View.GONE);
            submitBtn.setEnabled(true);
            isWaiting = false;

            if (isEditing) {
                // Return to review screen
                Intent intent = new Intent(this, ReviewAnswersActivity.class);
                intent.putExtra("tournamentId", tournamentId);
                intent.putExtra("answers", new HashMap<>(selectedAnswers));
                startActivity(intent);
                finish();
            } else {
                currentQuestionIndex++;
                if (currentQuestionIndex < questionList.size()) {
                    showQuestion();
                } else {
                    // Finished quiz → go to review
                    Intent intent = new Intent(this, ReviewAnswersActivity.class);
                    intent.putExtra("tournamentId", tournamentId);
                    intent.putExtra("answers", new HashMap<>(selectedAnswers));
                    startActivity(intent);
                    finish();
                }
            }
        }, 2500);
    }
}
