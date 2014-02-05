package sh.calaba.instrumentationbackend.actions.helpers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import sh.calaba.instrumentationbackend.Result;
import sh.calaba.instrumentationbackend.actions.Action;


public class ServerReachableAction implements Action {
  
  private static final int TIMEOUT = 5000;

  @Override
  public Result execute(String... args) {
    if (args != null && args.length > 0) {
      Result result = new Result(true, "OK");
      for (String url : args) {
        result.addBonusInformation(reachability(url));
      }
      return result;
    } else {
      return new Result(false, "no URLs specified");
    }
  }
  
  protected String reachability(String url) {
    try {
      if (url.startsWith("http:") || url.startsWith("https:")) {
        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
        c.setRequestMethod("HEAD");
        c.setUseCaches(false);
        c.setInstanceFollowRedirects(false);
        c.setDoInput(false);
        c.setDoOutput(false);
        c.setConnectTimeout(TIMEOUT);
        c.setReadTimeout(TIMEOUT);
        return "true " + c.getResponseCode();
      } else if (url.startsWith("tcp:")) {
        String[] parts = url.split(":", 3);
        Socket s = new Socket();
        try {
          s.setSoLinger(false, 0);
          s.connect(new InetSocketAddress(parts[1], Integer.valueOf(parts[2])), TIMEOUT);
        } finally {
          s.close();
        }
        return "true";
      } else {
        return "false invalid URL";
      }
    } catch (IOException e) {
      return "false " + e.getClass().getName();
    }
  }

  @Override
  public String key()
  {
    return "server_reachable";
  }
}
