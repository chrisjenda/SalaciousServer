package SalaciousServer.network.webserver.util;

public class Path {

  public static class Web {
    public static final String INDEX = "/index";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String ITEMS = "/items";
    public static final String ONE_ITEM = "/items/{basename}";
  }

  public static class Template {
    public static final String INDEX = "/velocity/index/index.vm";
    public static final String LOGIN = "/velocity/login/login.vm";
    public static final String ITEMS_ALL = "/velocity/item/all.vm";
    public static final String ITEMS_ONE = "/velocity/item/one.vm";
    public static final String NOT_FOUND = "/velocity/notFound.vm";
  }
}
