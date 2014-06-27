package org.jibberjabber.dialogue.model;

import java.util.ArrayList;

public class DialogueScript extends ArrayList<ScriptLine> {

    private static final long serialVersionUID = 5240588012716297119L;

    private int mCurrent = 0;

    public ScriptLine getCurrent() {
        return get(mCurrent);
    }

    public boolean next() {
        if (mCurrent < size() - 1) {
            mCurrent++;
            return true;
        }
        return false;
    }

    public boolean prev() {
        if (mCurrent > 0) {
            mCurrent--;
            return true;
        }
        return false;
    }
}
