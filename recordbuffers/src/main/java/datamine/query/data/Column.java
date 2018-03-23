package datamine.query.data;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The column is the basic data structure in the query expression.
 */
public class Column implements Serializable {

    private String name;
    private int type;
    private Operation operation;
    private String alias;

    private boolean hasAggregation = false;

    private Column(String name, int type, Operation operation, String alias) {
        this.name = name;
        this.type = type;
        this.operation = operation;
        this.alias = alias;
    }

    /**
     * Check if the column has aggregation function in its expression.
     * @return true if the column has aggregation function in its expression
     */
    public boolean hasAggregation() {
        return hasAggregation;
    }

    /**
     * Set the flag indicating there is aggregation function in the column.
     * @param hasAggregation the boolean flag to indicate if aggregation function exists
     */
    public void setHasAggregation(boolean hasAggregation) {
        this.hasAggregation = hasAggregation;
    }

    /**
     * Check if an operation has to be evaluated when getting the value of column.
     * @return true if an operation has to be evaluated when getting the value of column
     */
    public boolean hasOperation() {
        return operation != null;
    }

    /**
     * Get the operation if exists, null otherwise.
     * @return the operation if exists, null otherwise
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Set the operation through which the value of column can be evaluated.
     * @param operation the operation through which the value of column can be evaluated
     */
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    /**
     * Get the alias of the column.
     * @return the alias of the column
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Set the alias of the column.
     * @param alias the alias of the column
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Check if the column has an alias.
     * @return true if the column has an alias
     */
    public boolean hasAlias() {
        return StringUtils.isNotBlank(alias);
    }

    /**
     * Get the name of the column.
     * @return the name of the column
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the column.
     * @param name the name of the column
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the type ID of the column value.
     * Note that the definition of type can be found at {@link ValueUtils}.
     * @return the type ID of the column value
     */
    public int getType() {
        return type;
    }

    /**
     * Set the type ID of the column value.
     * Note that the definition of type can be found at {@link ValueUtils}.
     * @param type the type ID of the column value
     */
    public void setType(int type) {
        this.type = type;
    }


    /**
     * Get the ID of column.
     * @return the ID of column
     */
    public String getID() {
        if (operation == null) {
            return getName();
        } else {
            return operation.getID();
        }
    }

    /**
     * Check if the column can be calculated through
     * the input column map.
     * @param columnMap the input column map
     * @return true if the column can be calcuated by
     * the input column map, false otherwise.
     */
    public boolean applicable(Map<String, Column> columnMap) {

        if (columnMap.containsKey(getID())) {
            operation = null; // deduplicate the operations
            return true;
        } else if (operation != null) {
            return operation.applicable(columnMap);
        } else {
            return false;
        }
    }

    /**
     * Get the value of this column based on the input row.
     * @param row the row containing all necessary information during the query evaluation
     * @return an object of {@link Value} with the column value
     * @throws Exception an exception thrown if evaluation fails
     */
    public Value applyOperation(Row row) throws Exception {
        if (operation != null) {
            return operation.apply(row);
        } else {
            return row.getColumnValue(name);
        }
    }

    /**
     * Get the value of this column based on the input row and the index of the concerned
     * list-type value in row.
     * @param row the row containing all necessary information during the query evaluation
     * @param i the index of list-type value in the input row
     * @return an object of {@link Value} with the column value
     * @throws Exception an exception thrown if evaluation fails
     */
    public Value applyOperation(Row row, int i) throws Exception {
        if (operation != null) {
            return operation.apply(row, i);
        } else {
            Value listVal = row.getColumnValue(name);
            int listType = listVal.getType();
            if (listType == ValueUtils.ListLongDataType) {
                List list = (List) listVal.getValue();
                return new Value((long) list.get(i));
            } else if (listType == ValueUtils.ListFloatDataType) {
                List list = (List) listVal.getValue();
                return new Value((double) list.get(i));
            } else if (listType == ValueUtils.ListStringDataType) {
                List list = (List) listVal.getValue();
                return new Value((String) list.get(i));
            } else {
                // non-List type, then return the value directly
                return row.getColumnValue(name);
            }
        }
    }


    /**
     * Create an instance of {@link Column}.
     * @param name the name of column
     * @param type the type ID of the column value
     * @return an instance of {@link Column}
     */
    public static Column newInstance(String name, int type) {
        return new Column(name, type, null, null);
    }

    /**
     * Create an instance of {@link Column}.
     * @param name the name of column
     * @param operation the operation to get the column value
     * @return an instance of {@link Column}
     */
    public static Column newInstance(String name, Operation operation) {
        return new Column(name, operation.getResultValueType(), operation, null);
    }

    /**
     * Create an instance of {@link Column}.
     * @param name the column name
     * @param type the type ID of the column value
     * @param operation the operation to get the column value
     * @param alias the alias of the column
     * @return an instance of {@link Column}
     */
    public static Column newInstance(String name, int type, Operation operation, String alias) {
        return new Column(name, type, operation, alias);
    }

    /**
     * Create an instance of {@link Column} from another column instance.
     * @param col another column instance
     * @return an instance of {@link Column}
     */
    public static Column newInstance(Column col) {
        return new Column(col.getName(), col.getType(),
            col.operation, col.alias);
    }

}
