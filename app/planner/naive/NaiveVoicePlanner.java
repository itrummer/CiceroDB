package planner.naive;

import planner.ToleranceConfig;
import planner.VoiceOutputPlan;
import planner.VoicePlanner;
import planner.elements.TupleCollection;
import planner.elements.Scope;
import util.DatabaseUtilities;

import java.util.HashMap;
import java.util.Map;

/**
 * A naive implementation of a voice plan. Lists all results in a query as individual tuples.
 */
public class NaiveVoicePlanner extends VoicePlanner {
    protected ToleranceConfig config;

    public NaiveVoicePlanner() {
        setConfig(new ToleranceConfig(0, 0.0, 0));
    }

    @Override
    public VoiceOutputPlan executeAlgorithm(TupleCollection tupleCollection) {
        VoiceOutputPlan outputPlan = new VoiceOutputPlan();
        outputPlan.addScope(new Scope(tupleCollection.getTuples()));
        return outputPlan;
    }

    @Override
    public String getPlannerName() {
        return "naive";
    }

    public ToleranceConfig getConfig() {
        return config;
    }

    public void setConfig(ToleranceConfig config) {
        this.config = config;
    }


}
