package SalaciousServer.network.webserver.user;

import org.mindrot.jbcrypt.BCrypt;
import zombie.core.secure.PZcrypt;
import zombie.network.ServerWorldDatabase;

public class UserController {

  // Authenticate the user by hashing the inputted password using the stored salt,
  // then comparing the generated hashed password to the stored hashed password
  public static boolean authenticate(String username, String password) {
    if (username == null || password == null) {
      return false;
    }
    User user = UserPlayerDao.getUserByUsername(username);
    if (user == null) {
      return false;
    }
    String hashedPassword = PZcrypt.hash(ServerWorldDatabase.encrypt(password));
    return hashedPassword.equals(user.hashedPassword);
  }

  // This method doesn't do anything, it's just included as an example
  public static void setPassword(String username, String oldPassword, String newPassword) {
    if (authenticate(username, oldPassword)) {
      String newSalt = BCrypt.gensalt();
      String newHashedPassword = BCrypt.hashpw(newSalt, newPassword);
      // Update the user salt and password
    }
  }
}
