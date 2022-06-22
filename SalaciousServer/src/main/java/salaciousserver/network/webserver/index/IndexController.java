package salaciousserver.network.webserver.index;

import salaciousserver.network.webserver.user.UserPlayerDao;
import io.javalin.http.Handler;
import java.util.Map;

import salaciousserver.network.webserver.util.Path;
import salaciousserver.network.webserver.util.ViewUtil;

import static salaciousserver.network.webserver.WebServer.itemDao;

public class IndexController {
    public static Handler serveIndexPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("users", UserPlayerDao.getAllUserNames());
        model.put("item", itemDao.getRandomItem());
        ctx.render(Path.Template.INDEX, model);
    };
}