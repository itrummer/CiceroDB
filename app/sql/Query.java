package sql;

import planner.elements.TupleCollection;
import util.DatabaseUtilities;

import java.sql.SQLException;

public class Query {
    private String[] attributes;
    private String relation;
    private String condition;
    private Integer limit;

    public Query(String[] attributes, String relation, String condition, Integer limit) {
        this.attributes = attributes;
        this.relation = relation;
        this.condition = condition;
        this.limit = limit;
    }

    public Query(String[] attributes, String relation) {
        this(attributes, relation, null, null);
    }

    public Query(String[] attributes, String relation, int limit) {
        this(attributes, relation, null, limit);
    }

    public String getQuery() {
        String attributeList = attributes[0];
        for (int i = 1; i < attributes.length; i++) {
            attributeList += ", " + attributes[i];
        }
        return "SELECT " + attributeList + " FROM " + relation + (condition != null ? " WHERE " + condition : "") + (limit != null ? " LIMIT " + limit : "");
    }

    public String getRelation() {
        return relation;
    }

    public int getColumns() {
        return attributes.length;
    }

    public TupleCollection getTupleCollection() throws SQLException {
        return DatabaseUtilities.executeQuery(getQuery());
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
