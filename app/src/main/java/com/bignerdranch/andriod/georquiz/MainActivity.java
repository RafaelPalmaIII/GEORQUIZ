package com.bignerdranch.andriod.georquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bignerdranch.andriod.georquiz.Question;
import com.bignerdranch.andriod.georquiz.R;

public class MainActivity extends AppCompatActivity {

    private boolean[] mQuestionBankLocked;
    private static final String KEY_INDEX = "index";
    private static final String TAG = "MainActivity";
    private static final String KEY_CORRECT_ANSWERS = "correct_answers";
    private static final String KEY_ANSWERED_QUESTIONS = "answered_questions";
    private static final String KEY_ANSWERED_QUESTIONS_ARRAY = "answered_questions_array";
    private static final String KEY_LOCKED_QUESTIONS_ARRAY = "locked_questions_array";

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private int mCurrentIndex = 0;
    private int mCorrectAnswers = 0;
    private int mAnsweredQuestions = 0;
    private boolean[] mAnsweredQuestionsArray = new boolean[mQuestionBank.length];
    private boolean[] mLockedQuestionsArray = new boolean[mQuestionBank.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        mQuestionBankLocked = new boolean[mQuestionBank.length];



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCorrectAnswers = savedInstanceState.getInt(KEY_CORRECT_ANSWERS, 0);
            mAnsweredQuestions = savedInstanceState.getInt(KEY_ANSWERED_QUESTIONS, 0);
            mAnsweredQuestionsArray = savedInstanceState.getBooleanArray(KEY_ANSWERED_QUESTIONS_ARRAY);
            mLockedQuestionsArray = savedInstanceState.getBooleanArray(KEY_LOCKED_QUESTIONS_ARRAY);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
                updateQuestion();
            }
        });

        updateQuestion();
    }

    private void updateQuestion() {
        // Check if the quiz is finished
        if (mAnsweredQuestions == mQuestionBank.length) {
            // Disable the next and previous buttons when all questions are answered
            mNextButton.setEnabled(false);
            mPrevButton.setEnabled(false);
        } else {
            mNextButton.setEnabled(true);
            mPrevButton.setEnabled(true);
        }

        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        if (mAnsweredQuestionsArray[mCurrentIndex]) {
            disableAnswerButtons();
        } else {
            enableAnswerButtons();
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
            mCorrectAnswers++;
        } else {
            messageResId = R.string.incorrect_toast;
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        mAnsweredQuestions++;
        mAnsweredQuestionsArray[mCurrentIndex] = true;
        lockCurrentQuestion(mCurrentIndex);
        disableAnswerButtons();

        // Check if all questions are answered
        if (mAnsweredQuestions == mQuestionBank.length) {
            showScore(); // Show the score if all questions are answered
        }
    }

    private void disableAnswerButtons() {
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    private void enableAnswerButtons() {
        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);
    }

    private void showScore() {
        int percentage = (int) (((double) mCorrectAnswers / mQuestionBank.length) * 100);
        String message = "Your score: " + percentage + "%";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Reset the quiz state to start over
        resetQuizState();
    }


    private void lockCurrentQuestion(int currentIndex) {
        if (mQuestionBankLocked != null) {
            mQuestionBankLocked[currentIndex] = true;
        } else {
            // Handle the error, e.g., log it or display a message
            Log.e("MainActivity", "mQuestionBankLocked is null!");
        }
    }

    private void resetQuizState() {
        mCurrentIndex = 0;
        mCorrectAnswers = 0;
        mAnsweredQuestions = 0;

        // Reset the arrays for answered and locked questions
        for (int i = 0; i < mAnsweredQuestionsArray.length; i++) {
            mAnsweredQuestionsArray[i] = false;
            mLockedQuestionsArray[i] = false;
        }

        // Enable the answer buttons again
        enableAnswerButtons();

        // Update the question view
        updateQuestion();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         {
            if (resultCode == RESULT_OK) {
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(KEY_CORRECT_ANSWERS, mCorrectAnswers);
        savedInstanceState.putInt(KEY_ANSWERED_QUESTIONS, mAnsweredQuestions);
        savedInstanceState.putBooleanArray(KEY_ANSWERED_QUESTIONS_ARRAY, mAnsweredQuestionsArray);
        savedInstanceState.putBooleanArray(KEY_LOCKED_QUESTIONS_ARRAY, mLockedQuestionsArray);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

}