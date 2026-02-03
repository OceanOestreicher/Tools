package custom.striker.dao;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a parameterized SQL Statement for use with a {@link SqlDao}
 */
public class SqlStatement {
    private String sql;
    private final HashMap<String, Object> parameters;
    private PageRequest pageRequest;

    public SqlStatement(String sql) {
        this.sql = sql;
        parameters = new HashMap<>();
    }

    public boolean isSelectStatement() {
        return sql.startsWith("SELECT");
    }

    /**
     * Sets the value to be used for the named parameter. If the query has no parameters then this does nothing
     * @param name Name of the parameter to set the value for
     * @param value The value to set for the parameter
     */
    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }

    /**
     * Returns the constructed SQL string
     * @return The SQL string
     */
    public String getQueryString() {
        if (parameters.isEmpty()) {
            return sql;
        }

        for (String key : parameters.keySet()) {
            sql = sql.replaceAll(Pattern.quote(key), getObjectString(parameters.get(key)));
        }
        return sql;
    }

    private String getObjectString(Object value) {
        String returnValue = value.toString();

        if (value instanceof List) {
            StringBuilder result = new StringBuilder("(");
            List<?> values = (List<?>) value;

            for (Object item: values) {
                result.append(getObjectString(item));
                result.append(",");
            }

            result.replace(result.length() - 1, result.length(), ")");
            return result.toString();
        }

        if (value instanceof Boolean) {
            int converted = (Boolean) value ? 1 : 0;
            returnValue = Integer.toString(converted);
        }

        return "\"" + returnValue + "\"";
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }
}
