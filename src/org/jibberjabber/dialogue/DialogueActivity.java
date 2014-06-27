package org.jibberjabber.dialogue;

import java.util.Locale;

import org.jibberjabber.dialogue.model.ScriptLine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;

public class DialogueActivity extends Activity {

    private TextToSpeech mTtobj;

    private View mViewPrev;
    private View mViewRepeat;
    private View mViewNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogue);
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
                if (JibberJabberApplication.mScript.prev()) {
                    speakText(JibberJabberApplication.mScript.getCurrent());
                }
            }
        });

        mViewRepeat = findViewById(R.id.btn_repeat);
        mViewRepeat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                speakText(JibberJabberApplication.mScript.getCurrent());
            }
        });

        mViewNext = findViewById(R.id.btn_next);
        mViewNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JibberJabberApplication.mScript.next()) {
                    speakText(JibberJabberApplication.mScript.getCurrent());
                } else {
                    openNextScreen();
                }
            }

        });
    }

    public void speakText(ScriptLine scriptLine) {
        speakText(scriptLine.mText);
    }

    public void speakText(String toSpeak) {
        mTtobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void openNextScreen() {
        Intent intent = new Intent(this, QuestionsActivity.class);
        startActivity(intent);
    }

}
