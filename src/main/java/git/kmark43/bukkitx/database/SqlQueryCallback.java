package git.kmark43.bukkitx.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SqlQueryCallback {
    void onSuccess(ResultSet resultSet) throws SQLException;

    default void onFailure(SQLException e) {
        e.printStackTrace();
    }
}
