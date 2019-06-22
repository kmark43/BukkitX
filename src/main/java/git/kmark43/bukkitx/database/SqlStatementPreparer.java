package git.kmark43.bukkitx.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlStatementPreparer {
    PreparedStatement prepareQuery(Connection connection) throws SQLException;
}
