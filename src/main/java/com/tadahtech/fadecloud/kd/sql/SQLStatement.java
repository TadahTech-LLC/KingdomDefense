package com.tadahtech.fadecloud.kd.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public class SQLStatement {

    private String base;
    private Map<Integer, Object> map;

    public SQLStatement(String base) {
        this.base = base;
        this.map = new HashMap<>();
    }

    public SQLStatement set(int i, Object object) {
        this.map.put(i, object);
        return this;
    }

    public PreparedStatement prepare(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(base);
        for (Map.Entry<Integer, Object> entry : map.entrySet()) {
            int slot = entry.getKey();
            Object object = entry.getValue();
            if (object instanceof String) {
                String s = (String) object;
                statement.setString(slot, s);
                continue;
            }
            if (object instanceof Integer) {
                statement.setInt(slot, (Integer) object);
            }
        }
        return statement;
    }
}
