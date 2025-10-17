package com.courses.theredxlms;

import android.content.Intent; // <-- ADD THIS
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.io.Serializable; // <-- ADD THIS
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    // UI Elements (no changes here)
    private TextView textQuestionCounter, textQuestion;
    private RadioGroup radioGroupOptions;
    private RadioButton radioOption1, radioOption2, radioOption3, radioOption4;
    private Button btnNext, btnRetryQuiz, btnViewAnswers;
    private LinearProgressIndicator quizProgress;
    private LinearLayout quizContainer, scoreContainer;
    private TextView textFinalScore;

    // Quiz Logic
    private List<Question> questionList;
    private List<Question> quizQuestions; // <-- NEW: To store the 10 questions for this session
    private int currentQuestionIndex = 0;
    private int score = 0;
    private final int TOTAL_QUESTIONS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initializeViews();
        loadQuestions();
        startQuiz();

        btnNext.setOnClickListener(v -> handleNextButtonClick());
        btnRetryQuiz.setOnClickListener(v -> startQuiz());

        // --- CHANGE: Added logic for View Answers button ---
        btnViewAnswers.setOnClickListener(v -> {
            Intent intent = new Intent(QuizActivity.this, AnswersActivity.class);
            // Pass the list of questions to the new activity
            intent.putExtra("QUESTION_LIST", (Serializable) quizQuestions);
            startActivity(intent);
        });
    }

    private void initializeViews() {
        // ... no changes needed in this method
        quizContainer = findViewById(R.id.quiz_container);
        scoreContainer = findViewById(R.id.score_container);
        textQuestionCounter = findViewById(R.id.text_question_counter);
        textQuestion = findViewById(R.id.text_question);
        radioGroupOptions = findViewById(R.id.radio_group_options);
        radioOption1 = findViewById(R.id.radio_option_1);
        radioOption2 = findViewById(R.id.radio_option_2);
        radioOption3 = findViewById(R.id.radio_option_3);
        radioOption4 = findViewById(R.id.radio_option_4);
        btnNext = findViewById(R.id.btn_next);
        btnRetryQuiz = findViewById(R.id.btn_retry_quiz);
        btnViewAnswers = findViewById(R.id.btn_view_answers);
        quizProgress = findViewById(R.id.quiz_progress);
        textFinalScore = findViewById(R.id.text_final_score);
    }

    private void loadQuestions() {
        // ... no changes needed in this method
        questionList = new ArrayList<>();
        // --- AI Questions ---
        questionList.add(new Question("What does 'AI' stand for?", "Artificial Intelligence", "Automated Interface", "Advanced Internet", "Algorithmic Intuition", 1));
        questionList.add(new Question("Which of these is a subfield of AI?", "Machine Learning", "Cloud Computing", "Blockchain", "Cybersecurity", 1));
        questionList.add(new Question("Who is considered one of the 'fathers of AI'?", "Alan Turing", "Bill Gates", "Steve Jobs", "Tim Berners-Lee", 1));
        questionList.add(new Question("What is the goal of a 'Turing Test'?", "To test a machine's ability to exhibit intelligent behavior", "To test a computer's processing speed", "To test network security", "To test database efficiency", 1));
        questionList.add(new Question("A neural network is modeled after what?", "The human brain", "A spider web", "A computer circuit", "A social network", 1));
        questionList.add(new Question("What does NLP stand for in AI?", "Natural Language Processing", "Non-Linear Programming", "Network Layer Protocol", "New Logic Paradigm", 1));
        questionList.add(new Question("Which company developed the AI 'AlphaGo'?", "DeepMind (Google)", "Facebook AI", "OpenAI", "IBM", 1));
        questionList.add(new Question("What is 'supervised learning'?", "Learning from labeled data", "Learning without any data", "Learning from human feedback only", "Learning by watching videos", 1));
        questionList.add(new Question("Which of these is an application of Computer Vision?", "Facial Recognition", "Spell Check", "Music Recommendation", "Spam Filtering", 1));
        questionList.add(new Question("What is a 'chatbot'?", "An AI designed to simulate conversation", "A robot that cleans", "A security algorithm", "A data encryption tool", 1));
        // --- Coding Questions ---
        questionList.add(new Question("What does HTML stand for?", "HyperText Markup Language", "High-Level Text Management Language", "Hyperlink and Text Markup Language", "Home Tool Markup Language", 1));
        questionList.add(new Question("In Java, which keyword is used to create an object?", "new", "create", "object", "build", 1));
        questionList.add(new Question("Which symbol is used for comments in Python?", "#", "//", "/* */", "<!-- -->", 1));
        questionList.add(new Question("What is the primary purpose of CSS?", "To style web pages", "To handle server logic", "To manage databases", "To define web page structure", 1));
        questionList.add(new Question("Which data structure uses LIFO (Last-In, First-Out)?", "Stack", "Queue", "Array", "Linked List", 1));
        questionList.add(new Question("What is 'git'?", "A version control system", "A programming language", "A text editor", "A database", 1));
        questionList.add(new Question("In SQL, what keyword is used to retrieve data from a database?", "SELECT", "GET", "FETCH", "RETRIEVE", 1));
        questionList.add(new Question("Which of these is NOT a primitive data type in JavaScript?", "Object", "String", "Number", "Boolean", 1));
        questionList.add(new Question("What does API stand for?", "Application Programming Interface", "Advanced Programming Input", "Application Process Integration", "Automated Program Interface", 1));
        questionList.add(new Question("Which company developed the Kotlin programming language?", "JetBrains", "Google", "Microsoft", "Oracle", 1));
    }

    private void startQuiz() {
        score = 0;
        currentQuestionIndex = 0;
        Collections.shuffle(questionList); // Randomize the master list

        // --- NEW: Create a sublist of 10 questions for this quiz session ---
        quizQuestions = new ArrayList<>(questionList.subList(0, TOTAL_QUESTIONS));

        scoreContainer.setVisibility(View.GONE);
        quizContainer.setVisibility(View.VISIBLE);
        quizProgress.setMax(TOTAL_QUESTIONS);

        showNextQuestion();
    }

    private void showNextQuestion() {
        if (currentQuestionIndex < TOTAL_QUESTIONS) {
            radioGroupOptions.clearCheck();
            Question currentQuestion = quizQuestions.get(currentQuestionIndex); // Use the session list

            // --- CHANGE: Add question and option numbers ---
            String questionText = "Q" + (currentQuestionIndex + 1) + ": " + currentQuestion.getQuestionText();
            textQuestion.setText(questionText);
            radioOption1.setText("1. " + currentQuestion.getOption1());
            radioOption2.setText("2. " + currentQuestion.getOption2());
            radioOption3.setText("3. " + currentQuestion.getOption3());
            radioOption4.setText("4. " + currentQuestion.getOption4());

            textQuestionCounter.setText("Question " + (currentQuestionIndex + 1) + "/" + TOTAL_QUESTIONS);
            quizProgress.setProgress(currentQuestionIndex + 1);

            if (currentQuestionIndex == TOTAL_QUESTIONS - 1) {
                btnNext.setText("Finish");
            } else {
                btnNext.setText("Next");
            }
        } else {
            finishQuiz();
        }
    }

    private void handleNextButtonClick() {
        // ... no changes needed in this method
        int selectedOptionId = radioGroupOptions.getCheckedRadioButtonId();
        if (selectedOptionId == -1) {
            Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
            return;
        }
        checkAnswer(selectedOptionId);
        currentQuestionIndex++;
        showNextQuestion();
    }

    private void checkAnswer(int selectedId) {
        // --- CHANGE: Use the session list ---
        RadioButton selectedRadioButton = findViewById(selectedId);
        int selectedOptionNumber = radioGroupOptions.indexOfChild(selectedRadioButton) + 1;
        if (selectedOptionNumber == quizQuestions.get(currentQuestionIndex).getCorrectOptionNumber()) {
            score++;
        }
    }

    private void finishQuiz() {
        // ... no changes needed in this method
        quizContainer.setVisibility(View.GONE);
        scoreContainer.setVisibility(View.VISIBLE);
        textFinalScore.setText(String.format("You scored %02d/%d", score, TOTAL_QUESTIONS));
    }
}
