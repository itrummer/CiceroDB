package gui;

import util.DatabaseUtilities;
import planner.elements.TupleCollection;
import planner.linear.LinearProgrammingPlanner;
import planner.naive.NaiveVoicePlanner;
import planner.VoiceOutputPlan;
import planner.VoicePlanner;
import voice.VoiceGenerator;
import voice.WatsonVoiceGenerator;

import java.sql.SQLException;
import java.util.Scanner;

public class CommandLineUtility
{
    public static String PROMPT = "audiolizer> ";
    public static String QUIT_COMMAND = "\\q";
    public static String HELP_COMMAND = "\\h";
    public static String HELP_INFO = "Help: \n\tspecial commands: \\q = quit, \\h = help, set-planner <naive,lp>";

    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);
        VoiceGenerator voiceGenerator = new WatsonVoiceGenerator();
        VoicePlanner planner = new NaiveVoicePlanner();

        while(true) {
            System.out.print(PROMPT);
            String input = scanner.nextLine();
            if (input.length() == 0) {
                continue;
            }

            // check if user has input a command
            if (input.equals(QUIT_COMMAND)) {
                scanner.close();
                System.out.println("Bye");
                System.exit(0);
            } else if (input.equals(HELP_COMMAND)) {
                System.out.println(HELP_INFO);
                continue;
            } else if (input.startsWith("naive")) {
                planner = new NaiveVoicePlanner();
                System.out.println("Set VoicePlanner to Naive");
                continue;
            } else if (input.startsWith("lp")) {
                planner = new LinearProgrammingPlanner(2, 2.0, 2);
                System.out.println("Set VoicePlanner to LinearProgramming");
                continue;
            }

            try {
                TupleCollection results = DatabaseUtilities.executeQuery(input);
                if (results != null) {
                    VoiceOutputPlan outputPlan = planner.plan(results).getPlan();
                    if (outputPlan != null) {
                        String speechText = outputPlan.toSpeechText(false);
                        System.out.println(speechText);
                        voiceGenerator.generateSpeech(speechText);
                    } else {
                        System.err.println("Output plan was null");
                    }
                }
            } catch (SQLException e) {}
        }
    }
}