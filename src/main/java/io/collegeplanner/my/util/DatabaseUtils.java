package io.collegeplanner.my.util;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class DatabaseUtils {
    public static Jdbi getDatabaseConnection() {
        return DatabaseConnection.getDatabaseConnection()
                .installPlugin(new SqlObjectPlugin());
    }
}
