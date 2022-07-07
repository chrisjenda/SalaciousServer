package SalaciousServer.network.webserver;

import static io.javalin.apibuilder.ApiBuilder.*;

import SalaciousServer.network.webserver.index.IndexController;
import SalaciousServer.network.webserver.item.ItemController;
import SalaciousServer.network.webserver.item.ItemDao;
import SalaciousServer.network.webserver.user.UserPlayerDao;
import SalaciousServer.network.webserver.util.Filters;
import SalaciousServer.network.webserver.util.Path;
import SalaciousServer.network.webserver.util.ViewUtil;
import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.http.staticfiles.Location;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;

public class WebServer {
  public WebServer() {}

  public static ItemDao itemDao;
  public static UserPlayerDao userDao;
  public static boolean started;

  public void startserver() {
    DebugLog.log(DebugType.Network, "*** WEBSERVER ENABLED ****");

    // Instantiate your dependencies
    itemDao = new ItemDao();
    userDao = new UserPlayerDao();

    Javalin app =
        Javalin.create(
                config -> {
                  config.addStaticFiles("/public", Location.CLASSPATH);
                  config.registerPlugin(new RouteOverviewPlugin("/routes"));
                  config.showJavalinBanner = false;
                  // config.sessionHandler(Sessions::fileSessionHandler);
                })
            .start(7777);

    if (app != null) started = true;

    app.routes(
        () -> {
          before(Filters.stripTrailingSlashes);
          before(Filters.handleLocaleChange);
          before(LoginController.ensureLoginBeforeViewingITEMs);
          get("/", IndexController.serveIndexPage);
          get(Path.Web.INDEX, IndexController.serveIndexPage);
          get(Path.Web.ITEMS, ItemController.fetchAllItems);
          get(Path.Web.ONE_ITEM, ItemController.fetchOneItem);
          get(Path.Web.LOGIN, LoginController.serveLoginPage);
          post(Path.Web.LOGIN, LoginController.handleLoginPost);
          post(Path.Web.LOGOUT, LoginController.handleLogoutPost);
        });

    app.error(404, ViewUtil.notFound);

    /*
           app.accessManager((handler, ctx, roles) -> {
               String currentUser = ctx.sessionAttribute("current-user"); // retrieve user stored during login
               if (currentUser == null) {
                   redirectToLogin(ctx);
               } else if (userHasValidRole(ctx, roles)) {
                   handler.handle(ctx);
               } else {
                   throw new UnauthorizedResponse();
               }
           });

    */

    /*
           app.post("/login", ctx -> {
               String player = ctx.formParam("user");
               String password = PZcrypt.hash(ServerWorldDatabase.encrypt(ctx.formParam("pass")));
               String servername = zombie.network.GameServer.ServerName;

               String dbpath = ZomboidFileSystem.instance.getCacheDir() + "/db/" + servername + ".db";
               System.out.println("Path to serverDB: " + dbpath);

               File var3 = new File(dbpath);
               Connection var4 = null;
               if (!var3.exists()) {
                   System.out.println("ServerDB not found");
               } else {
                   var3.setReadable(true, false);
                   var4 = PZSQLUtils.getConnection(var3.getAbsolutePath());
               }
               try {
                   PreparedStatement var2 = var4.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND password = ?");
                   var2.setString(1, player);
                   var2.setString(2, password);
                   ResultSet var33 = var2.executeQuery();
                   if (var33.next()) {
                       String var44 = var33.getString("accessLevel");
                       var2.close();
                       System.out.println("Admin?: " + var44);
                   }

                   var2.close();
               } catch (SQLException var5) {
                   var5.printStackTrace();
               }

               String html = "";
               if (ServerWorldDatabase.isValidUserName(player)) {
                   IsoPlayer isoplayer = zombie.network.GameServer.getPlayerByUserName(player);
                   if (isoplayer == null) {
                       // TODO: Show player not online page

                   }
                   long steamid = isoplayer.getSteamID();
                   UdpConnection var6 = zombie.network.GameServer.getConnectionFromPlayer(isoplayer);
                   ServerWorldDatabase.LogonResult result = ServerWorldDatabase.instance.authClient(player, password,
                           var6.ip, steamid);
                   html = "Atempting to auth with Player: " + isoplayer.getDisplayName() + " Steamid: " + steamid +
                           " password: " + password + " Result: " + result.accessLevel;
                   ctx.html(html);
               }
           });

    */
  }
}
