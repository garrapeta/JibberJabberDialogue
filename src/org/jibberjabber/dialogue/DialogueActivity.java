package org.jibberjabber.dialogue;

import java.util.Locale;

import org.jibberjabber.dialogue.model.ScriptLine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class DialogueActivity extends Activity {

    private TextToSpeech mTtobj;

    private ImageView mImageView;

    private View mViewStart;
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
    public void onDestroy() {
        if (mTtobj != null) {
            mTtobj.stop();
            mTtobj.shutdown();
        }
        super.onDestroy();
    }

    private void bindViews() {

        mImageView = (ImageView) findViewById(R.id.image);

        mViewStart = findViewById(R.id.btn_start);
        mViewStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                speakText(JibberJabberApplication.mScript.getCurrent());
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

        mViewStart.setVisibility(View.VISIBLE);
        mViewRepeat.setVisibility(View.GONE);
        mViewNext.setVisibility(View.GONE);
    }

    public void speakText(ScriptLine scriptLine) {
        speakText(scriptLine.mText);

        if (scriptLine.mFirstCharacter) {
            mImageView.setImageResource(R.drawable.first);
        } else {
            mImageView.setImageResource(R.drawable.second);
        }
    }

    public void speakText(String toSpeak) {
        mTtobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

        mViewStart.setVisibility(View.GONE);
        mViewRepeat.setVisibility(View.VISIBLE);
        mViewNext.setVisibility(View.VISIBLE);
    }

    private void openNextScreen() {
        Intent intent = new Intent(this, QuestionsActivity.class);
        startActivity(intent);
    }

}
