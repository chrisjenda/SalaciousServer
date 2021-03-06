package salaciousserver;

//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.core.LoggerContext;
//import org.apache.logging.log4j.core.config.Configuration;
//import org.apache.logging.log4j.core.config.LoggerConfig;
//import org.apache.logging.log4j.io.IoBuilder;
import salaciousserver.network.webserver.WebServer;
import net.uptheinter.interceptify.annotations.InterceptClass;
import net.uptheinter.interceptify.annotations.OverwriteMethod;
import zombie.GameWindow;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.ZomboidNetData;
import salaciousserver.network.discordbot.DiscordBot;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Iterator;

import static salaciousserver.SalaciousConstants.*;
import static zombie.network.PacketTypes.PacketType.Login;



@InterceptClass("zombie.network.GameServer")
public class GameServerInterceptor {

    public static WebServer webserver;

    @OverwriteMethod("main")
    public static void main(Method parent, String[] args) {
/*
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        LoggerConfig rootLoggerConfig = config.getLoggers().get("");

        System.setOut(
                IoBuilder.forLogger(LogManager.getLogger("system.out"))
                        .setLevel(Level.INFO)
                        .buildPrintStream()
        );
        System.out.println("Lorem ipsum");


        Iterator it = rootLoggerConfig.getAppenders().entrySet().iterator();
        while (it.hasNext()) {
            System.out.println("test" + it.next().toString());
        }
        rootLoggerConfig.removeAppender("StormConsole");
        rootLoggerConfig.addAppender(config.getAppender("StormConsole"), Level.DEBUG, null);

        ctx.updateLoggers();

 */


        System.out.println("Salacious Enhanced Server " + SalaciousServerVersion + " Initialized");
        System.out.println("Salacious Discord Bot " + SalaciousDiscordVersion +" Initialized");
        webserver = new WebServer();
        webserver.startserver();
        if (WebServer.started)
            System.out.println("Salacious WebServer " + SalaciousWebServerVersion + " Initialized");
        else
            System.out.println("Salacious WebServer " + SalaciousWebServerVersion + " UNKNOWN ERROR:");

        try {
            Object invoke = parent.invoke(null, (Object) args);
        } catch (Exception ignored) {
        }
    }

    // TODO find a better hook that is after players have been updated to we can simply call "GameServer.getPlayers()"
    // Hook the Receive Login Function to alert the Bot that a user has joined
    @OverwriteMethod("receiveLogin")
    public static void receiveLogin(Method parent, ByteBuffer var0, UdpConnection var1, short var2) {
        //String s = StandardCharsets.UTF_8.decode(var0).toString();
        DiscordBot.onplayerConnected();
        try {
            Object invoke = parent.invoke(null, var0, var1, var2);
        } catch (Exception ignored) {
        }
    }

    // Hook the Receive Disconnect Function to alert the Bot that a user has left
    @OverwriteMethod("disconnect")
    public static void disconnect(Method parent, UdpConnection var1, String var2) {
        DiscordBot.onplayerDisconnect();
        try {
            Object invoke = parent.invoke(null, var1, var2);
        } catch (Exception ignored) {
        }
    }

    @OverwriteMethod("mainLoopDealWithNetData")
    public static void mainLoopDealWithNetData(Method parent, ZomboidNetData data) {
        var buffer = data.buffer.array();
        UdpEngine udpengine = zombie.network.GameServer.udpEngine;
        UdpConnection udp = udpengine.getActiveConnection(data.connection);

        DebugLog.log(DebugType.Network, "NETDATA HOOK: Packet Name: " + data.type.name());
        if(udp.username != null){
            // Packet is a player packet
            DebugLog.log(DebugType.Network, "NETDATA HOOK: Playername: " + udp.username);
        } else {
            // Packet is probably a login packet
            if (data.type == Login) {
                DebugLog.log(DebugType.Network, "NETDATA HOOK: Recieved Login Packet: ");
                // Reverse Enginer the Login Packet
                String playername = GameWindow.ReadString(data.buffer).trim();
                DebugLog.log(DebugType.Network, "NETDATA HOOK: playername: " + playername);
                data.buffer.clear();
                data.buffer.put(buffer);
                data.buffer.flip();
            }
        }

        try {
            Object invoke = parent.invoke(null, data);
        } catch (Exception ignored) {
        }
    }

}
