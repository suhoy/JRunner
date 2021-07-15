package suhoy.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author suh1995
 */
public class Utils {

    public static int getRand(int min, int max) {
        Random r = new Random();
        return (r.nextInt(max - min + 1) + min);
    }

    public static long getPacing(long min, long max) {
        if (min > max) {
            return 0;
        } else {
            return (long) (getRand((int) min, (int) max));
        }
    }

    /*
    public static <E> Optional<E> getRandom(Collection<E> e) {
        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst();
    }
     */
    public static CSVRecord getRandomCSV(ArrayList<CSVRecord> e) {
        return (CSVRecord) e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst().get();
    }

    public static ArrayList<?> toList(Iterable<?> records) {
        return (ArrayList) StreamSupport
                .stream(records.spliterator(), false)
                .collect(Collectors.toList());
    }
}
