package salaciousserver.network.webserver.item;

import io.javalin.http.Handler;
import java.util.Map;

import salaciousserver.network.webserver.util.Path;
import salaciousserver.network.webserver.util.ViewUtil;

import static salaciousserver.network.webserver.WebServer.itemDao;
import static salaciousserver.network.webserver.util.RequestUtil.*;

public class ItemController {

    public static Handler fetchAllItems = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("items", itemDao.getAllItems());
        ctx.render(Path.Template.ITEMS_ALL, model);
    };

    public static Handler fetchOneItem = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("item", itemDao.getItemByName(getParamBasename(ctx)));
        ctx.render(Path.Template.ITEMS_ONE, model);
    };
}