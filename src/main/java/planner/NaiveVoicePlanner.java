package planner;

import db.Row;
import db.RowCollection;

import java.util.ArrayList;

/**
 * A naive implementation of a voice plan. Lists all results in a query as individual tuples.
 */
public class NaiveVoicePlanner extends VoicePlanner {

    public static String EMPTY_ROW_COLLECTION_PLAN = "No results.";

    String result;

    @Override
    public void visit(RowCollection rowCollection) {
        result = "";
        ArrayList<Row> rows = rowCollection.getRows();
        if (rows.size() == 0) {
            result = EMPTY_ROW_COLLECTION_PLAN;
        } else {
            for (int i = 0; i < rows.size(); i++) {
                result += "Item " + (i + 1) + ": ";
                visit(rows.get(i));
            }
        }
    }

    @Override
    public void visit(Row row) {
        ArrayList<Object> values = row.getValues();
        for (int i = 0; i < values.size(); i++) {
            // TODO: incorporate column names in result
            result += values.get(i);
            if (i != values.size() - 1) {
                result += " ";
            }
        }
        result += ". ";
    }

    public String getResult() {
        if (result == null) {
            return "No rowCollection visited. No result to display";
        }
        return result;
    }
}