/*package SalaciousServer;

import SalaciousServer.network.webserver.WebServer;
import net.uptheinter.interceptify.annotations.InterceptClass;
import net.uptheinter.interceptify.annotations.OverwriteMethod;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;

import java.lang.reflect.Method;

@InterceptClass("zombie.debug.DebugLog")
public class Logging {


    @OverwriteMethod("log")
    public static void log(Method parent, DebugType var0, String var1) {
        System.out.println("LOL");
        try {
            Object invoke = parent.invoke(null, (DebugType) var0, (String) var1);
        } catch (Exception ignored) {
        }
    }
}

 */
