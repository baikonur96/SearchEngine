package searchengine.repositories;

import java.sql.Timestamp;

public class Utils {

    public static Timestamp getTimeStamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
