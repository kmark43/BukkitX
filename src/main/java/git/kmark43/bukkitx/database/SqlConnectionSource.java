package git.kmark43.bukkitx.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages a connection pool to a certain database
 */
public class SqlConnectionSource {
    private JavaPlugin plugin;
    private HikariDataSource dataSource;

    /**
     * Creates a new connection source
     * @param plugin The plugin maintaining this source
     * @param connectionUrl The connection string url
     */
    public SqlConnectionSource(JavaPlugin plugin, String connectionUrl) {
        this.plugin = plugin;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectionUrl);

        dataSource = new HikariDataSource(config);
    }

    /**
     * Creates a new connection source
     * @param plugin The plugin maintaining this source
     * @param connectionUrl The connection string url
     * @param user The database username
     * @param password The database password
     * @param useSSL Whether to use ssl to connect to the database
     */
    public SqlConnectionSource(JavaPlugin plugin, String connectionUrl, String user, String password, boolean useSSL) {
        this.plugin = plugin;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectionUrl);
        config.setUsername(user);
        config.setPassword(password);

        if (useSSL) {
            config.addDataSourceProperty("useSSL", true);
        }

        dataSource = new HikariDataSource(config);
    }

    /**
     * @return An instance of a connection which must be closed when finished with
     * @throws SQLException upon error retrieving a connection
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Executes a query asynchronously using a callback
     * @param preparer A function to prepare the statement to query with
     * @param callback A callback to handle the query results when retrieved
     */
    public void executeAsyncQuery(SqlStatementPreparer preparer, SqlQueryCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = preparer.prepareQuery(connection)) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        callback.onSuccess(resultSet);
                    }
                }
            } catch (SQLException e) {
                callback.onFailure(e);
            }
        });
    }
    /**
     * Executes an update asynchronously using a callback
     * @param preparer A function to prepare the statement to update with
     * @param callback A callback to respond after the update
     */
    public void executeAsyncUpdate(SqlStatementPreparer preparer, SqlUpdateCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = preparer.prepareQuery(connection)) {
                    statement.execute();
                    callback.onSuccess();
                }
            } catch (SQLException e) {
                callback.onFailure(e);
            }
        });
    }

    /**
     * Closes and cleans up the connection pool
     */
    public void close() {
        dataSource.close();
        dataSource = null;
    }
}
