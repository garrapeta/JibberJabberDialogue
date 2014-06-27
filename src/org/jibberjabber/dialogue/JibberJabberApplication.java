package org.jibberjabber.dialogue;

import org.jibberjabber.dialogue.model.DialogueQuestions;
import org.jibberjabber.dialogue.model.DialogueScript;
import org.jibberjabber.dialogue.model.Question;
import org.jibberjabber.dialogue.model.ScriptLine;

import android.app.Application;

public class JibberJabberApplication extends Application {

    public static DialogueScript mScript;
    public static DialogueQuestions mQuestions;

    static {
        mScript = new DialogueScript();

        mScript.add(new ScriptLine("How old are you?", true));
        mScript.add(new ScriptLine("I am 69 years old", false));
        mScript.add(new ScriptLine("Where are you from?", true));
        mScript.add(new ScriptLine("I am from the Moon", false));

        mQuestions = new DialogueQuestions();

        {
            Question q = new Question("How old is she?", "She is 69 years old");
            mQuestions.add(q);
        }
        {
            Question q = new Question("Where is she from?", "She is from the moon");
            mQuestions.add(q);
        }
    }

}
