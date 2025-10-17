package com.courses.theredxlms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    private final List<Question> questionList;

    // Constructor: This receives the list of questions from the Activity
    public AnswersAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This inflates the layout for a single row (item_answer.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        // This gets the question for the current row and tells the ViewHolder to display it
        Question question = questionList.get(position);
        holder.bind(question, position);
    }

    @Override
    public int getItemCount() {
        // This tells the RecyclerView how many items are in the list
        return questionList.size();
    }

    // The ViewHolder class holds the views for a single row
    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        private final TextView textItemQuestion;
        private final TextView textItemAnswer;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            // Finds the TextViews within the item_answer.xml layout
            textItemQuestion = itemView.findViewById(R.id.text_item_question);
            textItemAnswer = itemView.findViewById(R.id.text_item_answer);
        }

        // This method populates the TextViews with the correct data
        public void bind(Question question, int position) {
            String questionText = "Q" + (position + 1) + ": " + question.getQuestionText();
            String answerText = "Answer: " + question.getCorrectAnswerText();
            textItemQuestion.setText(questionText);
            textItemAnswer.setText(answerText);
        }
    }
}
