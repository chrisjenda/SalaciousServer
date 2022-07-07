package SalaciousServer.network.webserver.item;

import static SalaciousServer.network.webserver.WebServer.itemDao;
import static SalaciousServer.network.webserver.util.RequestUtil.getParamBasename;

import SalaciousServer.network.webserver.util.Path;
import SalaciousServer.network.webserver.util.ViewUtil;
import io.javalin.http.Handler;
import java.util.Map;

public class ItemController {

  public static Handler fetchAllItems =
      ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("items", itemDao.getAllItems());
        ctx.render(Path.Template.ITEMS_ALL, model);
      };

  public static Handler fetchOneItem =
      ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("item", itemDao.getItemByName(getParamBasename(ctx)));
        ctx.render(Path.Template.ITEMS_ONE, model);
      };
}
