package custom.striker.dao;

import custom.striker.service.ReflectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for interacting with a local Sqlite database
 */
public class SqliteDao implements SqlDao {

    private static final Logger log = LoggerFactory.getLogger(SqliteDao.class);
    private final String DATABASE_URL;
    // Used so that SQLite will use foreign keys since it doesn't by default
    private static final String FOREIGN_KEY_STATEMENT = "PRAGMA foreign_keys = ON";

    /**
     * Constructs a default instance of the DAO with a database configured to data/data.db
     */
    public SqliteDao() {
        this("data/data.db");
    }

    /**
     * Constructs an instance of the DAO with a database configured to databaseFilePath
     */
    public SqliteDao(String databaseFilePath) {
        DATABASE_URL = "jdbc:sqlite:" + databaseFilePath;
    }

    /** {@inheritDoc} */
    public ArraySet select(SqlStatement sqlStatement) {
        try (var conn = this.getConnection()) {
            if (!sqlStatement.isSelectStatement()) {
                log.error("Non-select statement passed to select. Query {}", sqlStatement.getQueryString());
                return null;
            }

            Statement statement = conn.createStatement();
            statement.execute(FOREIGN_KEY_STATEMENT);
            String queryString = constructQueryString(sqlStatement);

            log.debug("Executing query: {}", queryString);
            try (ResultSet result = statement.executeQuery(queryString)) {
                return new ArraySet(result);
            }

        } catch (Exception e) {
            log.error("Error processing SQL select.", e);
        }
        return null;
    }

    private String constructQueryString(SqlStatement statement) {
        if (statement.getPageRequest() == null) {
            return statement.getQueryString();
        }

        StringBuilder query = new StringBuilder(statement.getQueryString());
        PageRequest page = statement.getPageRequest();

        if (page != null) {
            String pageQuery = " LIMIT " + page.pageSize() + " OFFSET " + (page.pageSize() * page.page());
            query.append(pageQuery);
        }

        return query.toString();
    }

    /** {@inheritDoc} */
    public <T> List<T> select(SqlStatement sqlStatement, Class<T> klass) {
        ArraySet result = select(sqlStatement);
        if (result == null) {
            return List.of();
        }
        try {

            return result.stream()
                    .map(row -> {
                        Map<String, String> filteredRow = new HashMap<>();
                        for (String key: row.keySet()) {
                            if (row.get(key) == null || row.get(key).equals("null")) {
                                continue;
                            }
                            filteredRow.put(key, row.get(key));
                        }
                        return filteredRow;
                    })
                    .map(row -> ReflectionService.convertKeyValuePairsToObject(row, klass))
                    .toList();

        } catch (Exception e) {
            log.error("Error casting ArraySet to class {}", klass.getSimpleName(), e);
        }
        return List.of();
    }

    /** {@inheritDoc} */
    public boolean execute(SqlStatement sqlStatement) {
        try (var conn = this.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement statement = conn.createStatement()) {
                statement.execute(FOREIGN_KEY_STATEMENT);
                statement.execute(sqlStatement.getQueryString());
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw new Exception(e);
            }

        } catch (Exception e) {
            log.error("Error processing SQL execute.", e);
        }
        return false;
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(DATABASE_URL);
    }
}
