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

        mScript.add(new ScriptLine("Do you have any wild fantasies?", true));
        mScript.add(new ScriptLine("I have many, but my favourite is dressing up as a hippo", false));

        mScript.add(new ScriptLine("Do you like experimenting with new things in life?", true));
        mScript.add(new ScriptLine("Yes I have recently experimented with drugs", false));

        mScript.add(new ScriptLine("If you could be any fictional character who would you be?", true));
        mScript.add(new ScriptLine("Buffy the Vampire Slayer", false));

        mScript.add(new ScriptLine("What do you think your best characteristic is?", true));
        mScript.add(new ScriptLine("My general awesomeness", false));

        mScript.add(new ScriptLine("What adjective would you use to describe yourself?", true));
        mScript.add(new ScriptLine("Crapulous", false));

        mQuestions = new DialogueQuestions();

        {
            Question q = new Question("What is Jennifer's wildest fantasies?", "Dressing up as a hippo");
            mQuestions.add(q);
        }
        {
            Question q = new Question("What did Jennifer recently experiment with?", "with drugs");
            mQuestions.add(q);
        }
        {
            Question q = new Question("Which fictional character would she like to be?", "Buffy the Vampire Slayer");
            mQuestions.add(q);
        }
        {
            Question q = new Question("What is Jennifer's best characteristic?", "general awesomeness");
            mQuestions.add(q);
        }
        {
            Question q = new Question("What adjective does Jennifer use to describe her friend?", "She describes him as crapulous");
            mQuestions.add(q);
        }
    }

}
