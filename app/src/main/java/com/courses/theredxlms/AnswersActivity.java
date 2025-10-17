package com.courses.theredxlms;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AnswersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_answers);

        // Retrieve the list of questions passed from QuizActivity
        List<Question> questionList = (List<Question>) getIntent().getSerializableExtra("QUESTION_LIST");

        if (questionList != null) {
            // You will create this AnswersAdapter in the next step
            AnswersAdapter adapter = new AnswersAdapter(questionList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }
}
