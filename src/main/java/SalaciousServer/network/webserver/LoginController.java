package SalaciousServer.network.webserver;

import static SalaciousServer.network.webserver.util.RequestUtil.*;

import SalaciousServer.network.webserver.user.UserController;
import SalaciousServer.network.webserver.util.Path;
import SalaciousServer.network.webserver.util.ViewUtil;
import io.javalin.http.Handler;
import java.util.Map;

public class LoginController {

  public static Handler serveLoginPage =
      ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("loggedOut", removeSessionAttrLoggedOut(ctx));
        model.put("loginRedirect", removeSessionAttrLoginRedirect(ctx));
        ctx.render(Path.Template.LOGIN, model);
      };

  public static Handler handleLoginPost =
      ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        if (!UserController.authenticate(getQueryUsername(ctx), getQueryPassword(ctx))) {
          model.put("authenticationFailed", true);
          ctx.render(Path.Template.LOGIN, model);
        } else {
          ctx.sessionAttribute("currentUser", getQueryUsername(ctx));
          model.put("authenticationSucceeded", true);
          model.put("currentUser", getQueryUsername(ctx));
          if (getFormParamRedirect(ctx) != null) {
            ctx.redirect(getFormParamRedirect(ctx));
          }
          ctx.render(Path.Template.LOGIN, model);
        }
      };

  public static Handler handleLogoutPost =
      ctx -> {
        ctx.sessionAttribute("currentUser", null);
        ctx.sessionAttribute("loggedOut", "true");
        ctx.redirect(Path.Web.LOGIN);
      };

  // The origin of the request (request.pathInfo()) is saved in the session so
  // the user can be redirected back after login
  public static Handler ensureLoginBeforeViewingITEMs =
      ctx -> {
        if (!ctx.path().startsWith("/items")) {
          return;
        }
        if (ctx.sessionAttribute("currentUser") == null) {
          ctx.sessionAttribute("loginRedirect", ctx.path());
          ctx.redirect(Path.Web.LOGIN);
        }
      };
}
