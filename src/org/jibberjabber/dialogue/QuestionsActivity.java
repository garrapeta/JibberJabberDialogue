package org.jibberjabber.dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.jibberjabber.dialogue.model.Question;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class QuestionsActivity extends Activity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    private TextToSpeech mTtobj;

    private ImageView mImageView;

    private View mViewStart;
    private View mViewRepeat;
    private View mViewNext;

    private AudioManager mAudioManager;

    private HashMap<String, String> mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_questions);
        bindViews();

        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

        mParams = new HashMap<String, String>();
        mParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utterance_id");

        mTtobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mTtobj.setLanguage(Locale.UK);

                    mTtobj.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    onSpeechCompleted();
                                }
                            });
                        }
                    });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            // If Voice recognition is successful then it returns RESULT_OK
            if (resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    onRecognized(textMatchList);
                }
                // Result code for various error.
            } else {
                speakText(JibberJabberApplication.mQuestions.getCurrent());
            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void bindViews() {

        mImageView = (ImageView) findViewById(R.id.image);

        mViewStart = findViewById(R.id.btn_start);
        mViewStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                speakText(JibberJabberApplication.mQuestions.getCurrent());
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

        mViewStart.setVisibility(View.VISIBLE);
        mViewRepeat.setVisibility(View.GONE);
        mViewNext.setVisibility(View.GONE);

    }

    public void promptAnswer() {
        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage()
                                                                          .getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your answer");
        // intent.putExtra(RecognizerIntent.E, "Speak your answer");

        // Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        // // If number of Matches is not selected then return show toast
        // message
        // if (msTextMatches.getSelectedItemPosition() ==
        // AdapterView.INVALID_POSITION) {
        // Toast.makeText(this, "Please select No. of Matches from spinner",
        // Toast.LENGTH_SHORT)
        // .show();
        // return;
        // }

        int noOfMatches = 7;

        // Specify how many results you want to receive. The results will be
        // sorted where the first result is the one with higher confidence.

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

        mViewStart.setVisibility(View.GONE);
        mViewRepeat.setVisibility(View.GONE);
        mViewNext.setVisibility(View.GONE);
    }

    private void onRecognized(final ArrayList<String> textMatchList) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

                        String answer = JibberJabberApplication.mQuestions.getCurrent().mAnswer;
                        boolean success = isAnswerRight(textMatchList, answer);
                        if (success) {
                            MediaPlayer mediaPlayer = MediaPlayer.create(QuestionsActivity.this, R.raw.right);
                            mediaPlayer.start();
                            mImageView.setImageResource(R.drawable.tick_green);
                        } else {
                            mImageView.setImageResource(R.drawable.cross_red);
                            MediaPlayer mediaPlayer = MediaPlayer.create(QuestionsActivity.this, R.raw.wrong2);
                            mediaPlayer.start();
                        }

                        mViewStart.setVisibility(View.GONE);
                        mViewRepeat.setVisibility(View.VISIBLE);
                        mViewNext.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();

    }

    private boolean isAnswerRight(ArrayList<String> textMatchList, String answer) {
        Log.i("stp", "===========");
        Log.i("stp", ">>> " + answer.toLowerCase()
                                    .trim());

        for (String recognized : textMatchList) {
            if (matches(answer, recognized)) {
                return true;
            }
        }
        return false;
    }

    private boolean matches(String answer, String recognized) {

        if (answer == null) {
            return false;
        }
        if (recognized == null) {
            return false;
        }

        Log.i("stp", "<<< " + recognized.toLowerCase()
                                        .trim());

        return recognized.trim()
                         .equalsIgnoreCase(answer.trim());
    }

    public void speakText(Question question) {
        mImageView.setImageResource(R.drawable.question);
        speakText(question.mQuestion);
    }

    public void speakText(String toSpeak) {
        mTtobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, mParams);
    }

    private void onFinish() {
        showToastMessage("See you soon!");
    }

    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
             .show();
    }

    private void onSpeechCompleted() {
        promptAnswer();
    }

}
