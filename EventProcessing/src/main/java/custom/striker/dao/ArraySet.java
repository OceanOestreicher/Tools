package custom.striker.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;


/**
 * Data structure for holding the results of a SELECT query
 */
public class ArraySet implements Iterable<Map<String, String>> {

    private final List<Map<String, String>> result;

    public ArraySet(ResultSet set) throws SQLException {
        result = new ArrayList<>();

        while (set.next()) {
            result.add(unpack(set));
        }
    }

    private Map<String, String> unpack(ResultSet set) throws SQLException {
        Map<String, String> result = new HashMap<>();
        ResultSetMetaData metadata = set.getMetaData();
        final int columnCount = metadata.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            result.put(metadata.getColumnName(i), set.getString(i));
        }

        return result;
    }

    public int getRowCount() {
        return result.size();
    }

    @Override
    public Iterator<Map<String, String>> iterator() {
        return result.iterator();
    }

    public Stream<Map<String, String>> stream() {
        return result.stream();
    }
}
