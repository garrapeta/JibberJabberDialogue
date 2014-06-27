package org.jibberjabber.dialogue;

import java.util.Locale;

import org.jibberjabber.dialogue.model.Question;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class QuestionsActivity extends Activity {

    private TextToSpeech mTtobj;

    private View mViewPrev;
    private View mViewRepeat;
    private View mViewNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        bindViews();

        mTtobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mTtobj.setLanguage(Locale.UK);
                }
            }
        });
    }

    @Override
    public void onPause() {
        if (mTtobj != null) {
            mTtobj.stop();
            mTtobj.shutdown();
        }
        super.onPause();
    }

    private void bindViews() {
        mViewPrev = findViewById(R.id.btn_prev);
        mViewPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JibberJabberApplication.mQuestions.prev()) {
                    speakText(JibberJabberApplication.mQuestions.getCurrent());
                }
            }
        });

        mViewRepeat = findViewById(R.id.btn_repeat);
        mViewRepeat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                speakText(JibberJabberApplication.mQuestions.getCurrent());
            }
        });

        mViewNext = findViewById(R.id.btn_next);
        mViewNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JibberJabberApplication.mQuestions.next()) {
                    speakText(JibberJabberApplication.mQuestions.getCurrent());
                } else {
                    onFinish();
                }
            }

        });
    }

    public void speakText(Question question) {
        speakText(question.mQuestion);
    }

    public void speakText(String toSpeak) {
        mTtobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void onFinish() {
        Toast.makeText(this, "See you soon!", Toast.LENGTH_SHORT);
    }

}
