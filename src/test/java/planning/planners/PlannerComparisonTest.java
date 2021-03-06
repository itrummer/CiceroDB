package planning.planners;

import planning.PlanningResult;
import planning.config.Config;
import planning.elements.TupleCollection;

/**
 * Testing for the relationship between various planners
 */
public class PlannerComparisonTest extends PlannerTestBase {

    public void testLinearPerformsAsWellAsNaive() throws Exception {
        TupleCollection tuples = sqlConnector.buildTupleCollectionFromQuery("select * from restaurants limit 10", "Restaurants");

        Config config = createConfig(2, 2, 2.5);
        config.setTimeout(120);

        PlanningResult linearResult = planningManager.buildPlan(linearPlanner, tuples, config);
        PlanningResult naiveResult = planningManager.buildPlan(naivePlanner, tuples, config);

        int linearCost = linearResult.getPlan().toSpeechText(true).length();
        int naiveCost = naiveResult.getPlan().toSpeechText(true).length();

        assertTrue(linearCost <= naiveCost);
    }

    public void testHybridTop10PerformsAsWellAsNaive() throws Exception {
        TupleCollection tuples = sqlConnector.buildTupleCollectionFromQuery("select * from restaurants limit 10", "Restaurants");

        Config config = createConfig(2, 2, 2.5);
        config.setTimeout(120);

        PlanningResult hybridResult = planningManager.buildPlan(hybridPlannerTop10, tuples, config);
        PlanningResult naiveResult = planningManager.buildPlan(naivePlanner, tuples, config);

        int hybridCost = hybridResult.getPlan().toSpeechText(true).length();
        int naiveCost = naiveResult.getPlan().toSpeechText(true).length();

        assertTrue(hybridCost <= naiveCost);
    }

    public void testHybridTupleCoveringPerformsAsWellAsNaive() throws Exception {
        TupleCollection tuples = sqlConnector.buildTupleCollectionFromQuery("select * from restaurants limit 10", "Restaurants");

        Config config = createConfig(2, 2, 2.5);
        config.setTimeout(120);

        PlanningResult hybridResult = planningManager.buildPlan(hybridPlannerTupleCovering, tuples, config);
        PlanningResult naiveResult = planningManager.buildPlan(naivePlanner, tuples, config);

        int hybridCost = hybridResult.getPlan().toSpeechText(true).length();
        int naiveCost = naiveResult.getPlan().toSpeechText(true).length();

        assertTrue(hybridCost <= naiveCost);
    }

    public void testGreedyPerformsAsWellAsNaive() throws Exception {
        TupleCollection tuples = sqlConnector.buildTupleCollectionFromQuery("select * from restaurants limit 10", "Restaurants");

        Config config = createConfig(2, 2, 2.5, 0.1);
        config.setTimeout(120);

        PlanningResult greedyResult = planningManager.buildPlan(greedyPlanner, tuples, config);
        PlanningResult naiveResult = planningManager.buildPlan(naivePlanner, tuples, config);

        int greedyCost = greedyResult.getPlan().toSpeechText(true).length();
        int naiveCost = naiveResult.getPlan().toSpeechText(true).length();

        assertTrue(greedyCost <= naiveCost);
    }

    public void testLinearPerformsAsWellAsHybridTop10() throws Exception {
        TupleCollection tuples = sqlConnector.buildTupleCollectionFromQuery("select * from restaurants limit 10", "Restaurants");

        Config config = createConfig(2, 2, 2.5);
        config.setTimeout(120);

        PlanningResult linearResult = planningManager.buildPlan(linearPlanner, tuples, config);
        PlanningResult hybridResult = planningManager.buildPlan(hybridPlannerTop10, tuples, config);

        int linearCost = linearResult.getPlan().toSpeechText(true).length();
        int hybridCost = hybridResult.getPlan().toSpeechText(true).length();

        assertTrue(linearCost <= hybridCost);
    }

    public void testLinearPerformsAsWellAsHybridTupleCovering() throws Exception {
        TupleCollection tuples = sqlConnector.buildTupleCollectionFromQuery("select * from restaurants limit 10", "Restaurants");

        Config config = createConfig(2, 2, 2.5);
        config.setTimeout(120);

        PlanningResult linearResult = planningManager.buildPlan(linearPlanner, tuples, config);
        PlanningResult hybridResult = planningManager.buildPlan(hybridPlannerTupleCovering, tuples, config);

        int linearCost = linearResult.getPlan().toSpeechText(true).length();
        int hybridCost = hybridResult.getPlan().toSpeechText(true).length();

        assertTrue(linearCost <= hybridCost);
    }

    public void testLinearPerformsAsWellAsGreedy() throws Exception {
        TupleCollection tuples = sqlConnector.buildTupleCollectionFromQuery("select * from restaurants limit 10", "Restaurants");

        Config config = createConfig(2, 2, 2.5, 0.1);
        config.setTimeout(120);

        PlanningResult linearResult = planningManager.buildPlan(linearPlanner, tuples, config);
        PlanningResult fantomResult = planningManager.buildPlan(greedyPlanner, tuples, config);

        int linearCost = linearResult.getPlan().toSpeechText(true).length();
        int fantomCost = fantomResult.getPlan().toSpeechText(true).length();

        assertTrue(linearCost <= fantomCost);
    }
}
