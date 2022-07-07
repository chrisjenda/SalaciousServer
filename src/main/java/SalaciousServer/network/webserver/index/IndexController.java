package SalaciousServer.network.webserver.index;

import static SalaciousServer.network.webserver.WebServer.itemDao;

import SalaciousServer.network.webserver.user.UserPlayerDao;
import SalaciousServer.network.webserver.util.Path;
import SalaciousServer.network.webserver.util.ViewUtil;
import io.javalin.http.Handler;
import java.util.Map;

public class IndexController {
  public static Handler serveIndexPage =
      ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("users", UserPlayerDao.getAllUserNames());
        model.put("item", itemDao.getRandomItem());
        ctx.render(Path.Template.INDEX, model);
      };
}
