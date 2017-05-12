package planner.elements;

import planner.Speakable;

import java.util.*;

/**
 * Represents a tuple in a database table
 */
public class Tuple implements Speakable {
    List<String> attributes;
    Map<String, Value> valueAssignments;
    String cachedLongFormResultWithoutContext;

    /**
     * Constructor for a Tuple with a list of String attributes
     */
    public Tuple(ArrayList<String> attributes) {
        this.attributes = attributes;
        this.valueAssignments = new HashMap<>();
        this.cachedLongFormResultWithoutContext = null;
    }

    /**
     * Adds a ValueAssignment to this Tuple.
     * @param column The attribute or column name for the value
     * @param value The value for the new value assignment
     */
    public void addValueAssignment(String column, Value value) {
        valueAssignments.put(column, value);
    }

    public List<String> getAttributes() {
        return attributes;
    }

    /**
     * Retrieves the value for the specified attribute
     */
    public Value valueForAttribute(String attribute) {
        return valueAssignments.get(attribute);
    }

    public String toSpeechText(boolean inLongForm) {
        return toSpeechText(null, inLongForm);
    }

    /**
     * Calculates the speech text for speaking this Tuple in a given Context. When given a nonnull
     * Context, we eliminate outputting attribute-value pairs for which the Context fixes a domain.
     * @param c The Context in which to output this Tuple
     * @return The speech representation of this Tuple within the given Context
     */
    public String toSpeechText(Context c, boolean inLongForm) {
        if (c == null && inLongForm && cachedLongFormResultWithoutContext != null) {
            return cachedLongFormResultWithoutContext;
        }
        String result = "";
        boolean firstAttribute = true;
        for (String attribute : attributes) {
            if (c == null || !c.isAttributeFixed(attribute)) {
                Value v = valueForAttribute(attribute);
                if (firstAttribute) {
                    result += v.toSpeechText(inLongForm);
                } else {
                    result += ", " + v.toSpeechText(inLongForm) + " " + attribute;
                }
                firstAttribute = false;
            }
        }
        if (c == null && inLongForm) {
            cachedLongFormResultWithoutContext = result;
        }
        return result;
    }
}
