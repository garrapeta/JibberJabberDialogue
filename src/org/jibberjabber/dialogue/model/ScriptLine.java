package org.jibberjabber.dialogue.model;

public class ScriptLine {

    public final String mText;
    public final boolean mFirstCharacter;

    public ScriptLine(String line, boolean firstCharacter) {
        mText = line;
        mFirstCharacter = firstCharacter;
    }
}
