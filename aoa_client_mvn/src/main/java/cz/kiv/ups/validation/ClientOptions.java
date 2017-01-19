package cz.kiv.ups.validation;


import cz.kiv.ups.application.Application;
import cz.kiv.ups.application.Logger;
import cz.kiv.ups.config.ConnectionConfig;
import cz.kiv.ups.partial.Tools;

import java.util.HashMap;
import java.util.Map;

public class ClientOptions {

    private static final Logger logger = Logger.getLogger();


    public static final String OPT_PORT = "p";

    public static final String OPT_IP = "i";

    public static final String OPT_QUIET = "q";

    public static final String OPT_HELP = "h";


    private static final String[] OPT_PORT_FLAGS = new String[]{"-p", "--port"};

    private static final String[] OPT_IP_FLAGS = new String[]{"-i", "--ip"};

    private static final String[] OPT_QUIET_FLAGS = new String[]{"-q", "--quiet"};

    private static final String[] OPT_HELP_FLAGS = new String[]{"-h", "--help"};


    private Map<String, String> options;


    private StringBuilder sb;

    private String[] args;

    private int argc;


    public ClientOptions(String[] args) {
        this.args = args;
        this.argc = this.args.length;
        this.init();
        this.parse();
    }

    private void init() {
        this.options = new HashMap<>();
//        this.options.put(OPT_IP, "0");
//        this.options.put(OPT_PORT, "0");
//        this.options.put(OPT_QUIET, "0");
//        this.options.put(OPT_HELP, "0");

        this.sb = new StringBuilder();
    }

    private void parse() {
        int i = 0;

        while(i < this.argc) {

            boolean isOpt = false;
            String opt = this.args[i];

            if(this.isOption(opt, ClientOptions.OPT_PORT_FLAGS)) {
                String p = this.validatePort(this.args[++i]);
                if(p != null) this.options.put(OPT_PORT, String.valueOf(p));
                isOpt = true;
            } else if(this.isOption(opt, ClientOptions.OPT_IP_FLAGS)) {
                String ip = this.validateIP(this.args[++i]);
                if(ip != null) this.options.put(OPT_IP, ip);
                isOpt = true;
            } else if(this.isOption(opt, ClientOptions.OPT_QUIET_FLAGS)) {
                this.options.put(OPT_QUIET, "1");
                isOpt = true;
            } else if(this.isOption(opt, ClientOptions.OPT_HELP_FLAGS)) {
                this.options.put(OPT_HELP, "1");
                isOpt = true;
            }

            if(!isOpt) {
                logger.error("Invalid input arguments.");
                this.printHelp();
                Application.disconnect(true, null);
            }

            i++;
        }
    }




    public boolean has(String opt) {
        return this.options.containsKey(opt);
    }

    private boolean isOption(String arg, String[] opt) {
        //logger.debug("checking: " + arg + " (" + opt[0] + ", " + opt[1] + ")");
        return arg.equals(opt[0]) || arg.equals(opt[1]);
    }

    private String validatePort(String port) {
        if(Tools.isValidPort(port))
            return port;

        logger.error("Invalid port inserted. DEFAULT is used.");
        return null;
    }

    private String validateIP(String ip){
        if(Tools.isValidIP(ip))
            return ip;
        logger.error("Invalid IP inserted. DEFAULT is used.");
        return null;
    }

    public void printHelp() {
        this.sb.setLength(0);
        this.sb.append("usage: ./aoa_client [options]\n\nOptions:\n");

        // help
        this.sb.append(OPT_HELP_FLAGS[0]);
        this.sb.append(", ");
        this.sb.append(OPT_HELP_FLAGS[1]);
        this.sb.append("                  Displays help information.\n");

        // ip
        this.sb.append(OPT_IP_FLAGS[0]);
        this.sb.append(" <ip>, ");
        this.sb.append(OPT_IP_FLAGS[1]);
        this.sb.append(" <ip>          Specifies an IP address of the server you are\n");
        this.sb.append("                            connecting to. Valid IP or \"localhost\" are accepted.\n");

        //port
        this.sb.append(OPT_PORT_FLAGS[0]);
        this.sb.append(" <port>, ");
        this.sb.append(OPT_PORT_FLAGS[1]);
        this.sb.append(" <port>    Specifies the server to run at <port> port\n");
        this.sb.append("                            in a range ");
        this.sb.append(ConnectionConfig.SERVER_PORT_MIN);
        this.sb.append(" - ");
        this.sb.append(ConnectionConfig.SERVER_PORT_MAX);
        this.sb.append(".\n");

        // quiet
        this.sb.append(OPT_QUIET_FLAGS[0]);
        this.sb.append(", ");
        this.sb.append(OPT_QUIET_FLAGS[1]);
        this.sb.append("                 Specifies the server to run at quiet mode.\n");

        logger.info(this.sb.toString());
    }


    public String get(String flag) {
        return this.options.get(flag);
    }
}
