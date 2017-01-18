package cz.kiv.ups.partial;

import static cz.kiv.ups.config.ConnectionConfig.SERVER_PORT_MAX;
import static cz.kiv.ups.config.ConnectionConfig.SERVER_PORT_MIN;

public class Tools {

    public static boolean isNumber(String str){
        return str.matches("^([0-9]*)$");
    }

    public static boolean isNumberInRange(String str, int lower, int upper){
        if(!Tools.isNumber(str))
            return false;

        int num = Integer.parseInt(str);

        return num >= lower && num <= upper;
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

        return (int) sum;
    }
}
