package datamine.query.data;

import java.io.Serializable;
import java.util.Map;

/**
 * This interface defines the behaviors that all operations should have.
 */
public interface Operation extends Serializable {

    /**
     * Apply the operation to the input and generate
     * result.
     *
     * @param input an instance of {@link Row}
     * @return The result as {@link Value}
     * @throws Exception an exception is thrown if the operation fails.
     */
    Value apply(Row input) throws Exception;

    /**
     * Apply the operation to the input and generate
     * result, when the type of column is ListXXXDataType.
     *
     * @param input an instance of {@link Row}
     * @param i     the index of the to-be-evaluated element in the list
     * @return The result as {@link Value}
     * @throws Exception Exception an exception is thrown if the operation fails.
     */
    Value apply(Row input, int i) throws Exception;


    /**
     * Check if the operation can be applied to the input columnMap.
     *
     * @param columnMap the input column map
     * @return true if the operation is valid given the input column map.
     */
    boolean applicable(Map<String, Column> columnMap);

    /**
     * Get the result data type of the operation.
     *
     * Note that the type can be found in {@link ValueUtils}
     */
    int getResultValueType();

    /**
     * Get the ID of operation.
     *
     * @return the ID of operation
     */
    String getID();

}