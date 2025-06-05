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

public class ReviewAnswersActivity extends AppCompatActivity {

    private LinearLayout answersContainer;
    private Button submitBtn;
    private FirebaseFirestore db;
    private List<Question> questionList = new ArrayList<>();
    private Map<Integer, String> selectedAnswers = new HashMap<>();
    private String tournamentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_answers);

        answersContainer = findViewById(R.id.answersContainer);
        submitBtn = findViewById(R.id.finalSubmitBtn);

        db = FirebaseFirestore.getInstance();
        tournamentId = getIntent().getStringExtra("tournamentId");
        selectedAnswers = (Map<Integer, String>) getIntent().getSerializableExtra("answers");

        loadQuestions();

        submitBtn.setOnClickListener(v -> showResults());
    }

    private void loadQuestions() {
        db.collection("tournaments")
                .document(tournamentId)
                .collection("questions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    questionList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Question q = doc.toObject(Question.class);
                        questionList.add(q);
                    }
                    displayReview();
                });
    }

    private void displayReview() {
        answersContainer.removeAllViews();

        for (int i = 0; i < questionList.size(); i++) {
            Question q = questionList.get(i);
            String selected = selectedAnswers.get(i);

            TextView qText = new TextView(this);
            qText.setText("Q" + (i + 1) + ": " + q.question);
            qText.setTextSize(16);
            qText.setPadding(0, 24, 0, 4);
            answersContainer.addView(qText);

            TextView aText = new TextView(this);
            aText.setText("Your Answer: " + (selected != null ? selected : "(None)"));
            aText.setTextSize(15);
            aText.setPadding(0, 0, 0, 8);
            aText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            answersContainer.addView(aText);

            Button editBtn = new Button(this);
            editBtn.setText("Edit Answer");
            int finalI = i;
            editBtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, PlayerQuizActivity.class);
                intent.putExtra("tournamentId", tournamentId);
                intent.putExtra("isEditing", true);
                intent.putExtra("editIndex", finalI);
                intent.putExtra("answers", new HashMap<>(selectedAnswers));
                startActivity(intent);
                finish();
            });
            answersContainer.addView(editBtn);
        }
    }

    private void showResults() {
        int score = 0;
        List<Integer> correctQ = new ArrayList<>();
        List<Integer> incorrectQ = new ArrayList<>();

        for (int i = 0; i < questionList.size(); i++) {
            String selected = selectedAnswers.get(i);
            if (selected != null && selected.equals(questionList.get(i).correct_answer)) {
                score++;
                correctQ.add(i + 1);
            } else {
                incorrectQ.add(i + 1);
            }
        }

        answersContainer.removeAllViews();

        TextView resultText = new TextView(this);
        resultText.setText("✅ Score: " + score + "/" + questionList.size()
                + "\n\n✅ Correct Questions: " + correctQ
                + "\n❌ Incorrect Questions: " + incorrectQ
                + "\n\n🎉 Thanks for playing!");
        resultText.setTextSize(18);
        resultText.setPadding(0, 20, 0, 20);

        answersContainer.addView(resultText);
        submitBtn.setVisibility(View.GONE);
    }
}
