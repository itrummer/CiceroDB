package planner;

import planner.elements.Scope;

import java.util.ArrayList;

/**
 * Representation of a VoiceOutputPlan
 */
public class VoiceOutputPlan implements Speakable {
    ArrayList<Scope> scopes;
    String cachedResult;

    public VoiceOutputPlan(ArrayList<Scope> scopes) {
        this.scopes = scopes;
    }

    public VoiceOutputPlan() {
        this(new ArrayList<Scope>());
    }

    public void addScope(Scope scope) {
        scopes.add(scope);
    }

    public ArrayList<Scope> getScopes() {
        return scopes;
    }

    /**
     * Formats this VoiceOutputPlan to speech text suitable for a VoiceGenerator
     * @return The String representation of the speech output for this plan
     */
    public String toSpeechText() {
        if (cachedResult != null) {
            return cachedResult;
        }

        StringBuilder builder = new StringBuilder("");

        for (Scope scope : scopes) {
            if (scope.getContext() == null) {
                builder.append(scope.toSpeechText() + "\n");
            }
        }

        for (Scope scope : scopes) {
            if (scope.getContext() != null) {
                builder.append(scope.toSpeechText() + "\n");
            }
        }

        return builder.toString();
    }

}
