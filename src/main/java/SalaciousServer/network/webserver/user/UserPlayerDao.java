package SalaciousServer.network.webserver.user;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.ServerWorldDatabase;
import zombie.util.PZSQLUtils;

public class UserPlayerDao {

  private static ResultSet parseDB(String sql, String... params) throws SQLException {

    // Build path to server database file
    String dbpath =
        ZomboidFileSystem.instance.getCacheDir()
            + "/db/"
            + zombie.network.GameServer.ServerName
            + ".db";

    DebugLog.log(DebugType.Network, "WEBSERVER: Received Inquiry for database: " + sql);

    // Check if database exists
    File db = new File(dbpath);
    if (!db.exists()) {
      DebugLog.log(DebugType.Network, "WEBSERVER: ERROR: ServerDB not found at path: " + dbpath);
    } else {

      DebugLog.log(DebugType.Network, "WEBSERVER: Params Count: " + params.length);

      // Open an SQL Connection to server database file
      DebugLog.log(DebugType.Network, "WEBSERVER: Path to serverDB: " + dbpath);
      db.setReadable(true, false);
      Connection sqlconnection = PZSQLUtils.getConnection(db.getAbsolutePath());

      // Prepare Query with provided Query Statement
      PreparedStatement query = sqlconnection.prepareStatement(sql);

      // Add Parms if Provided
      if (params.length > 0) {
        DebugLog.log(DebugType.Network, "WEBSERVER: Query Params: " + Arrays.toString(params));
        int index = 1;
        for (String param : params) {
          query.setString(index, param);
          index++;
        }
      }
      ResultSet queryresult = query.executeQuery();
      RowSetFactory factory = RowSetProvider.newFactory();
      CachedRowSet rowset = factory.createCachedRowSet();
      rowset.populate(queryresult);
      queryresult.close();
      query.close();
      DebugLog.log(DebugType.Network, "WEBSERVER: Finished query result: " + rowset);
      return rowset;
    }
    return null;
  }

  private static boolean isValidPlayer(String player) {
    return ServerWorldDatabase.isValidUserName(player);
  }

  private boolean isPlayerOnline(String player) {
    return zombie.network.GameServer.getPlayerByUserName(player) != null;
  }

  public static User getUserByUsername(String username) {
    DebugLog.log(DebugType.Network, "WEBSERVERDBG: Getting User: " + username);
    if (isValidPlayer(username)) {
      try {
        ResultSet result = parseDB("SELECT * FROM whitelist WHERE username = ?", username);
        if (result.next()) {
          DebugLog.log(DebugType.Network, "WEBSERVERDBG: Querying User: " + username);
          String playername = result.getString("username");
          String hashedpass = result.getString("password");
          String accesslevel = result.getString("accesslevel");
          DebugLog.log(
              DebugType.Network,
              "WEBSERVERDBG: User Data Result: "
                  + " Name: "
                  + playername
                  + " Password: "
                  + hashedpass
                  + " Access Level: "
                  + accesslevel);

          return new User(playername, hashedpass, accesslevel);
        } else {
          DebugLog.log(DebugType.Network, "WEBSERVERDBG: ERROR: While Getting User: " + username);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static Iterable<User> getAllUserNames() {

    DebugLog.log(DebugType.Network, "WEBSERVERDBG: Getting All Users: ");
    List<User> users = new ArrayList<User>();
    try {
      ResultSet result = parseDB("SELECT * FROM whitelist");

      if (result == null) {
        DebugLog.log(DebugType.Network, "WEBSERVERDBG: ERROR: While Getting All Users: ");
        return null;
      }

      while (result.next()) {
        String playername = result.getString("username");
        String hashedpass = result.getString("password");
        String accesslevel = result.getString("accesslevel");
        DebugLog.log(
            DebugType.Network,
            "WEBSERVERDBG: User Data Result: "
                + " Name: "
                + playername
                + " Password: "
                + hashedpass
                + " Access Level: "
                + accesslevel);
        users.add(new User(playername, hashedpass, accesslevel));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return users;
  }
}
