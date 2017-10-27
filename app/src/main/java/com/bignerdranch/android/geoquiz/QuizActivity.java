package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";   //Creates a string which the log method calls upon to determine which class we are working with.
    private static final String KEY_INDEX = "index";    //Creates key for key-value-pair allowing us to save state in bundle when rotating device.
    private static final String KEY_CHEAT = "cheat";   //Creates key for key pair value allowing to save whether user cheated
    private static final int REQUEST_CODE_CHEAT = 0;    //Creates a int which is sent to a child activity and received back to check who is sending it.

    private Button mTrueButton;             //Holds reference to True Button
    private Button mFalseButton;            //Holds reference to False Button
    private Button mNextButton;             //Holds reference to Next Button
    private Button mCheatButton;            //Holds reference to Cheat Button
    private TextView mQuestionTextView;     //Holds reference to QuestionTextView ( the space where question is displayed )

    private int mScore = 0;                     //Keeps score for game.

    //Creates an array of questions for the app to draw from
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    //Keeps track of the current question being displayed
    private int mCurrentIndex = 0;

    private boolean[] mIsCheater = new boolean[mQuestionBank.length];   //An array that keeps track of which questions you cheated on.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log method displays state of app. First parameter points to the source of the message and the second its content.
        Log.d(TAG, "onCreate(Bundle) called");

        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater[mCurrentIndex] = savedInstanceState.getBoolean(KEY_CHEAT, false);
        }

        //Grabs a reference to the question text box
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();

        //Get a referrence to the view object true_button.
        mTrueButton = (Button) findViewById(R.id.true_button);

        //Create a listener to inform  you when the button known as mTrueButton has been pressed.
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                //Keeps user from answering multiple times.
                mTrueButton.setClickable(false);
                mFalseButton.setClickable(false);
                Log.d(TAG, "mTrueButton and mFalseButton was disabled.");
            }
        });

        //Get a referrence to the view object false_button.
        mFalseButton = (Button) findViewById(R.id.false_button);
        //Create a listener to inform  you when the button known as mFalseButton has been pressed.
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                checkAnswer(false);
                //Keeps user from answering multiple times.
                mFalseButton.setClickable(false);
                mTrueButton.setClickable(false);
                Log.d(TAG, "mFalseButton and mTrueButton was disabled.");
            }
        });

        //Grabs reference to the Next Button then sets a listener for user click input.
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
                mFalseButton.setClickable(true);
                Log.d(TAG, "mFalseButton was enabled.");
                mTrueButton.setClickable(true);
                Log.d(TAG, "mTrueButoon was enabled.");
                finalScore();
            }
        });

        //Grabs reference to the Cheat Button then sets a  listener for the user click input.
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }

            mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
        }
    }


    //Saves mCurrentState in Bundle (set of key-value-pairs) so when screen rotates app doesnt reset.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_CHEAT, mIsCheater[mCurrentIndex]);
    }


    //Private method which updates the question text in the app
    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    //Private method which checks the users answer against the correct answer stored in the Question Model.
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (mIsCheater[mCurrentIndex] == true) {
            messageResId = R.string.judgment_toast;
        } else {

            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;

                //Increments score if answer is correct.
                mScore++;
            } else {
                messageResId = R.string.incorrect_toast;
            }

        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    //Keeps track of then displays the final score.
    private void finalScore() {

        String answer = mScore + "/" + mQuestionBank.length;

        if (mCurrentIndex == 0) {
            Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
            mScore = 0;
            Log.d(TAG, "Quiz was completed.");
        }
    }

    //Logs activity of app when switching states.
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    //Logs activity of app when switching states.
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    //Logs activity of app when switching states.
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    //Logs activity of app when switching states.
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    //Logs activity of app when switching states.
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
