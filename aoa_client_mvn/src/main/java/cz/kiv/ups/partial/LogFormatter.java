package cz.kiv.ups.partial;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private static final DateFormat df = new SimpleDateFormat("hh:mm:ss.SSS");


    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();

        Level lvl = record.getLevel();
        builder.append("[").append(lvl).append(lvl == Level.INFO ? "  " : "").append("] ");
        builder.append("[").append(df.format(new Date(record.getMillis()))).append("]: ");
        builder.append(formatMessage(record)).append("\n");
        //builder.append("(").append(record.getSourceClassName()).append(".");
        //builder.append(record.getSourceMethodName()).append(")");
        //builder.append("\n");
        return builder.toString();
    }

    public String getHead(Handler h) {
        return super.getHead(h);
    }

    public String getTail(Handler h) {
        return super.getTail(h);
    }
}
