package custom.striker.dao;

import java.util.List;

/**
 * Interface for a class that interacts with a MySQL database
 */
public interface SqlDao {

    /**
     * Performs a select operation using the provided statement
     * @param sqlStatement Select statement to execute
     * @return an {@link ArraySet} containing the results of the select query
     */
    ArraySet select(SqlStatement sqlStatement);

    /**
     * Performs a Select operation using the provided statement and casts it to the provided class
     * @param sqlStatement Select statement to execute
     * @param klass Class to cast the result back to
     * @return A list of objects corresponding to the provided class
     * @param <T> Type of class to transform the data into
     */
    <T> List<T> select(SqlStatement sqlStatement, Class<T> klass);

    /**
     * Executes the provided statement, rolling back if the transaction fails
     * @param sqlStatement Statement to execute
     * @return True if the statement executes correctly, false otherwise
     */
    boolean execute(SqlStatement sqlStatement);
}
