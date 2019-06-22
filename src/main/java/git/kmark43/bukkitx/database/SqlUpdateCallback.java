package git.kmark43.bukkitx.database;

import java.sql.SQLException;

public interface SqlUpdateCallback {
    void onSuccess() throws SQLException;

    default void onFailure(SQLException e) {
        e.printStackTrace();
    }
}
