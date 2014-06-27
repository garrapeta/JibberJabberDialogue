package org.jibberjabber.dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.jibberjabber.dialogue.model.Question;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class QuestionsActivity extends Activity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    private TextToSpeech mTtobj;

    private View mViewRepeat;
    private View mViewNext;

    private HashMap<String, String> mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        bindViews();

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
                    if (textMatchList.get(0)
                                     .contains("search")) {

                        String searchQuery = textMatchList.get(0)
                                                          .replace("search", " ");
                        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                        search.putExtra(SearchManager.QUERY, searchQuery);
                        startActivity(search);
                    } else {
                        // populate the Matches
                        onRecognized(textMatchList);
                    }

                }
                // Result code for various error.
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                showToastMessage("Audio Error");
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                showToastMessage("Client Error");
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void bindViews() {

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

    public void promptAnswer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage()
                                                                          .getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your answer");

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

        int noOfMatches = 4;

        // Specify how many results you want to receive. The results will be
        // sorted where the first result is the one with higher confidence.

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    private void onRecognized(ArrayList<String> textMatchList) {
        String answer = JibberJabberApplication.mQuestions.getCurrent().mAnswer;
        boolean success = isAnswerRight(textMatchList, answer);
        if (success) {
            showToastMessage("WELL DONE!!!");
        } else {
            showToastMessage("BAD!!!");
        }
    }

    private boolean isAnswerRight(ArrayList<String> textMatchList, String answer) {
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
        return recognized.trim()
                         .equalsIgnoreCase(answer.trim());
    }

    public void speakText(Question question) {
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
