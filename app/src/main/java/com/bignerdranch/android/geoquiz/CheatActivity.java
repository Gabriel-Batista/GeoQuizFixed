package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";   //Key for KEY-VALUE pair as a constant
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";       //Key for KEY-PAIR value which sends info to see if user cheated
    private static final String TAG = "QuizActivity";                                                       //Creates a string which the log method calls upon to determine which class we are working with.
    private static final String KEY_CHEATED = "cheated";

    private boolean mCheater;
    private boolean mAnswerIsTrue;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    //Create a new method which creates an intent with the extra already attached
    public static Intent newIntent(Context packageContext, boolean answerIsTrue)    {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);

        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        //Get the extra that was sent with question answer
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        //Create reference to answer text view
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

        //Create reference to show answer button
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);

        //Create listener for button and display cheat.
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAnswerIsTrue)   {
                    mAnswerTextView.setText(R.string.true_button);
                }else{
                    mAnswerTextView.setText(R.string.false_button);
                }

                setAnswerShownResult(true);
            }
        });

        if(savedInstanceState != null)   {
            setAnswerShownResult(savedInstanceState.getBoolean(KEY_CHEATED, false));

            if(mAnswerIsTrue)   {
                mAnswerTextView.setText(R.string.true_button);
            }else{
                mAnswerTextView.setText(R.string.false_button);
            }
        }
    }

    private void setAnswerShownResult(boolean isAnswerShown)   {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);

        mCheater = true;
    }

    //Saves mCurrentState in Bundle (set of key-value-pairs) so when screen rotates app doesnt reset.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putBoolean(KEY_CHEATED, mCheater);
    }
}