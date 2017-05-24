package planner;

import junit.framework.TestCase;
import planner.elements.TupleCollection;
import planner.greedy.GreedyPlanner;
import planner.hybrid.ContextPruner;
import planner.hybrid.HybridPlanner;
import planner.hybrid.TupleCoveringPruner;
import planner.hybrid.UsefulPruner;
import planner.linear.LinearProgrammingPlanner;
import planner.naive.NaiveVoicePlanner;
import util.CSVBuilder;
import util.DatabaseUtilities;
import util.Utilities;
import voice.WatsonVoiceGenerator;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Unit testing for a VoicePlanner
 */
public class VoicePlannerTest extends TestCase {
    public enum TestCase {
        QUERY_1("model, dollars, pounds, inch_display", "macbooks"),
        QUERY_2("restaurant, price, rating, cuisine", "restaurants"),
        QUERY_3("restaurant, price, cuisine", "restaurants"),
        QUERY_4("model, gigabytes_of_memory, gigabytes_of_storage, dollars", "macbooks"),
        QUERY_5("restaurant, rating, location, cuisine", "yelp"),
        QUERY_6("restaurant, rating, price, reviews, location, cuisine", "yelp");

        private String attributeList;
        private String relation;

        TestCase(String attributeList, String relation) {
            this.attributeList = attributeList;
            this.relation = relation;
        }

        public String getQuery() {
            return "SELECT " + attributeList + " FROM " + relation;
        }

        public String getRelation() {
            return relation;
        }

        public TupleCollection getTupleCollection() throws SQLException {
            return DatabaseUtilities.executeQuery(getQuery());
        }
    }

    public void printResult(String plannerName, TestCase testCase, PlanningResult result) {
        System.out.println(plannerName + " planner executed " + testCase.name() +
                " on " + testCase.getRelation() + " " + result.getExecutionCount() +
                " times and took an average "  + result.getAverageExecutionTimeSeconds() +
                " seconds per execution");
    }

    public void testNaivePlanner() throws Exception {
        VoicePlanner planner = new NaiveVoicePlanner();
        for (TestCase testCase : TestCase.values()) {
            PlanningResult result = planner.plan(testCase.getTupleCollection(), 100);
            printResult(planner.getPlannerName(), testCase, result);
        }
    }

    public void testGreedyAllTestCases100Times() throws Exception {
        VoicePlanner planner = new GreedyPlanner(2, 1.5, 1);
        for (TestCase testCase : TestCase.values()) {
            PlanningResult result = planner.plan(testCase.getTupleCollection(), 100);
            printResult(planner.getPlannerName(), testCase, result);
        }
    }

    public void testGreedyPlannerQuery1() throws Exception {
        GreedyPlanner planner = new GreedyPlanner(2, 1.5, 1);
        PlanningResult result;
        result = planner.plan(TestCase.QUERY_1.getTupleCollection(), 10);
        printResult(planner.getPlannerName(), TestCase.QUERY_2, result);
        result = planner.plan(TestCase.QUERY_1.getTupleCollection(), 100);
        printResult(planner.getPlannerName(), TestCase.QUERY_2, result);
        result = planner.plan(TestCase.QUERY_1.getTupleCollection(), 1000);
        printResult(planner.getPlannerName(), TestCase.QUERY_2, result);
    }

    public void testGreedyPlannerOver10ExecutionsQuery2() throws Exception {
        GreedyPlanner planner = new GreedyPlanner(2, 1.5, 1);
        PlanningResult result;
        result = planner.plan(TestCase.QUERY_2.getTupleCollection(), 10);
        printResult(planner.getPlannerName(), TestCase.QUERY_2, result);
        result = planner.plan(TestCase.QUERY_2.getTupleCollection(), 100);
        printResult(planner.getPlannerName(), TestCase.QUERY_2, result);
        result = planner.plan(TestCase.QUERY_2.getTupleCollection(), 1000);
        printResult(planner.getPlannerName(), TestCase.QUERY_2, result);
    }

    public void testHybridAllTestCases100Times() throws Exception {
        HybridPlanner planner = new HybridPlanner(new TupleCoveringPruner(30), 3, 2.0, 1);
        for (TestCase testCase : TestCase.values()) {
            PlanningResult result = planner.plan(testCase.getTupleCollection(), 100);
            printResult(planner.getPlannerName(), testCase, result);
        }
    }

    public void testGreedyPlannerQuery6() throws Exception {
        GreedyPlanner planner = new GreedyPlanner(2, 2.0, 1);
        PlanningResult result = planner.plan(DatabaseUtilities.executeQuery(TestCase.QUERY_6.getQuery()));
        printResult(planner.getPlannerName(), TestCase.QUERY_6, result);
    }

    /**
     * Executes each planner with the same configuration and asserts that the linear algorithm's
     * output plan has the smallest speech cost
     * @param k The k value to use in configuring the HybridPlanner
     */
    public void testLinearHasSmallestCost(int mS, double mW, int mC, int k) throws Exception {
        VoicePlanner linear = new LinearProgrammingPlanner(mS, mW, mC);
        VoicePlanner greedy = new GreedyPlanner(mS, mW, mC);
        VoicePlanner hybrid = new HybridPlanner(new TupleCoveringPruner(k), mS, mW, mC);

        for (TestCase testCase : new TestCase[] {TestCase.QUERY_1, TestCase.QUERY_2}) {
            PlanningResult result;
            TupleCollection tupleCollection = testCase.getTupleCollection();

            result = linear.plan(tupleCollection);
            System.out.println("Linear\n" + result.getPlan().toSpeechText(false));
            int linearCost = result.getPlan().toSpeechText(true).length();

            result = greedy.plan(tupleCollection);
            int greedyCost = result.getPlan().toSpeechText(true).length();
            System.out.println("Greedy\n" + result.getPlan().toSpeechText(false));
            assertTrue(testCase.name(),linearCost <= greedyCost);

            result = hybrid.plan(tupleCollection);
            int hybridCost = result.getPlan().toSpeechText(true).length();
            System.out.println("Hybrid\n" + result.getPlan().toSpeechText(false));
            assertTrue(testCase.name(), linearCost <= hybridCost);
        }
    }

    public void testLinearHasSmallestCostManyConfigs() throws Exception {
        testLinearHasSmallestCost(2, 2.0, 2, 10);
        testLinearHasSmallestCost(2, 2.0, 2, 30);
        testLinearHasSmallestCost(2, 3.0, 2, 30);
        testLinearHasSmallestCost(3, 2.0, 2, 10);
        testLinearHasSmallestCost(2, 1.0, 2, 10);
        testLinearHasSmallestCost(3, 1.5, 2, 10);
    }

    /**
     * Executes each test case for each planner-config combination
     */
    public String executeTests(TestCase[] testCases, ToleranceConfig[] configs, ContextPruner[] pruners) throws Exception {
        CSVBuilder csvBuilder = new CSVBuilder();
        WatsonVoiceGenerator voiceGenerator = new WatsonVoiceGenerator();
        NaiveVoicePlanner naiveVoicePlanner = new NaiveVoicePlanner();
        LinearProgrammingPlanner linearProgrammingPlanner = new LinearProgrammingPlanner();
        HybridPlanner hybridPlanner = new HybridPlanner();
        GreedyPlanner greedyPlanner = new GreedyPlanner();

        for (TestCase testCase : testCases) {
            TupleCollection tupleCollection = testCase.getTupleCollection();
            String basePath = "/Users/mabryan/Desktop/" + testCase.name() + "_";

            PlanningResult naiveResult = naiveVoicePlanner.plan(tupleCollection);
            csvBuilder.addTestResult(naiveVoicePlanner, naiveResult, naiveResult, tupleCollection, testCase.name());
            voiceGenerator.generateAndWriteToFile(naiveResult.getPlan().toSpeechText(false),
                    basePath + naiveVoicePlanner.getPlannerName() + ".wav");

            for (int c = 0; c < configs.length; c++) {
                ToleranceConfig config = configs[c];

                linearProgrammingPlanner.setConfig(config);
                PlanningResult linearResult = linearProgrammingPlanner.plan(tupleCollection);
                int linearCost = linearResult.getPlan().speechCost();
                csvBuilder.addTestResult(linearProgrammingPlanner, linearResult, naiveResult, tupleCollection, testCase.name());
                voiceGenerator.generateAndWriteToFile(linearResult.getPlan().toSpeechText(false),
                        basePath + linearProgrammingPlanner.getPlannerName() + "-config" + c + ".wav");

                hybridPlanner.setConfig(config);
                for (int p = 0; p < pruners.length; p++) {
                    hybridPlanner.setContextPruner(pruners[p]);
                    PlanningResult hybridResult = hybridPlanner.plan(tupleCollection);
                    assertTrue(hybridResult.getPlan().speechCost() >= linearCost);
                    csvBuilder.addTestResult(hybridPlanner, hybridResult, naiveResult, tupleCollection, testCase.name());
                    voiceGenerator.generateAndWriteToFile(hybridResult.getPlan().toSpeechText(false),
                            basePath + hybridPlanner.getPlannerName() + "-config" + c + ".wav");
                }

                greedyPlanner.setConfig(config);
                PlanningResult greedyResult = greedyPlanner.plan(tupleCollection);
                assertTrue(greedyResult.getPlan().speechCost() >= linearCost);
                csvBuilder.addTestResult(greedyPlanner, greedyResult, naiveResult, tupleCollection, testCase.name());
                voiceGenerator.generateAndWriteToFile(greedyResult.getPlan().toSpeechText(false),
                        basePath + greedyPlanner.getPlannerName() + "-config" + c + ".wav");

            }
        }
        return csvBuilder.getCSVString();
    }

    public void testPlannerGroup1() throws Exception {
        TestCase[] testCases = new TestCase[] {
//                TestCase.QUERY_1,
//                TestCase.QUERY_2,
//                TestCase.QUERY_3,
//                TestCase.QUERY_4,
                TestCase.QUERY_5,
        };

        ToleranceConfig[] configs = new ToleranceConfig[] {
                new ToleranceConfig(2, 2.0, 2),
                new ToleranceConfig(2, 2.0, 1),
                new ToleranceConfig(2, 3.0, 1)
        };

        ContextPruner[] pruners = new ContextPruner[] {
                new TupleCoveringPruner(15)
        };


        String csvResult = executeTests(testCases, configs, pruners);
        Utilities.writeStringToFile("/Users/mabryan/Desktop/output.csv", csvResult);
    }

}