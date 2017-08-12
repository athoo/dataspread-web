package org.model;

import org.zkoss.util.logging.Log;

import java.sql.*;

public class DBListener  extends Thread {
    private static final Log _logger = Log.lookup(DBListener.class);
    private boolean keepRunning;

    public DBListener()
    {
        keepRunning=true;
    }

    @Override
    public void run() {
        createEventTable();
        try {
            listenEvents();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stopListener() {
        keepRunning=false;
    }

    private void listenEvents() throws InterruptedException, SQLException {
        // Assume session id 0 is created
        _logger.info("Listening for Events");
        Connection connection = DBHandler.instance.getConnection();

        PreparedStatement listen_events = connection.prepareStatement(
                "DELETE FROM db_events "
                + "RETURNING action, data");

        while (true) {
            boolean no_records = true;
            ResultSet rs = listen_events.executeQuery();
            while (rs.next()) {
                no_records = false;
                System.out.println("Got message " + rs.getString(1) + " " + rs.getString(2));
                handleEvenet(rs.getInt(1), rs.getString(2));
                connection.commit();
            }
            rs.close();
            connection.commit();
            if (no_records)
                Thread.sleep(200L);
        }
    }

    private void handleEvenet(int event, String data) {
        switch (event)
        {
            case 1: // Table refresh
                break;
            default:

        }
    }

    private void createEventTable()
    {
        try (Connection connection = DBHandler.instance.getConnection();
             Statement stmt = connection.createStatement())
        {
            String createTable = "CREATE TABLE  IF NOT  EXISTS  db_events (" +
                    "action INTEGER NOT NULL," +
                    "data TEXT);";
            stmt.execute(createTable);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
