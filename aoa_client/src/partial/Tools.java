package partial;

import static config.ConnectionConfig.SERVER_PORT_MAX;
import static config.ConnectionConfig.SERVER_PORT_MIN;

public class Tools {

    public static boolean isNumber(String str){
        return str.matches("^([0-9]*)$");
    }

    public static boolean isValidIP(String str) {
        return str.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")
                || str.equals("localhost");
    }

    public static boolean isValidPort(String str) {
        int port;

        if(!Tools.isNumber(str))
            return false;

        port = Integer.parseInt(str);
        return port >= SERVER_PORT_MIN && port <= SERVER_PORT_MAX;
    }

    public static int checksum(String str, int mod) {
        long sum = 0;

        for(int i = 0; i < str.length(); ++i) {
            sum += (long) str.charAt(i);
        }

        if(mod > 0)
            sum = sum % mod;

        System.out.println("checksum'of \"" + str + "\": " + sum);
        return (int) sum;
    }
}