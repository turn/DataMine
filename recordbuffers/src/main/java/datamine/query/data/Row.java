package datamine.query.data;

import gnu.trove.map.hash.THashMap;

import java.io.Serializable;

/**
 * The class depicts the data structure of saving all intermediate and final results
 * in the query execution.
 */
public class Row implements Serializable {

    private THashMap<String, Value> columnValueMap = new THashMap<>();

    private Row() {}

    /**
     * Get the value of a given column.
     * @param colName the column name
     * @return the value of a given column
     */
    public Value getColumnValue(String colName) {
        return columnValueMap.get(colName);
    }

    /**
     * Set the value of a given column.
     * @param colName the column name
     * @param v the value to set
     */
    public void setColumnValue(String colName, Value v) {
        columnValueMap.put(colName, v);
    }

    /**
     * Remove all values from the row.
     */
    public void clear() {
        columnValueMap.clear();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (String s : columnValueMap.keySet()) {
            if (columnValueMap.get(s) != null) {
                sb.append(s).append(" = ").append(columnValueMap.get(s).toString())
                    .append(" type = ").append(columnValueMap.get(s).getType())
                    .append("\n");
            } else {
                sb.append(s).append(" = null ").append("\n");
            }
        }
        return sb.toString();
    }


    /**
     * Concatenate the column values into a string with given delimiter.
     * @param columns an array of columns
     * @param delimiter a delimiter between column values
     * @return a string of column values
     */
    public String getValuesInText(Column[] columns, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (Column col : columns) {
            Value val = columnValueMap.get(col.getID());
            String valTxt = "";
            if (val != null) {
                valTxt = val.toString();
            }

            if (sb.length() == 0) {
                sb.append(valTxt);
            } else {
                sb.append(delimiter).append(valTxt);
            }
        }
        return sb.toString();
    }

    /**
     * Create an instance of {@link Row}.
     * @return an instance of {@link Row}
     */
    public static Row newInstance() {
        return new Row();
    }

    /**
     * Create an instance by copying an existing instance of {@link Row}.
     * @param row an instance of {@link Row}
     * @return a copy of the input {@link Row} instance
     */
    public static Row newInstance(Row row) {
        Row ret = newInstance();
        ret.columnValueMap.putAll(row.columnValueMap);
        return ret;
    }

    /**
     * Create an instance of {@link Row} composed of the input columns.
     * Note that the input arrays should be correlated. For instance,
     * the first element of fields should be the name of column in the
     * first place in cols.
     *
     * @param fields an array of field names
     * @param cols an array of columns
     * @return an instance of {@link Row}
     */
    public static Row newInstance(String[] fields, Column[] cols) {
        Row ret = newInstance();
        for (int i=0; i<cols.length; ++i) {
            Column curCol = cols[i];
            ret.setColumnValue(curCol.getID(), new Value(fields[i], curCol.getType()));
        }
        return ret;
    }

}
