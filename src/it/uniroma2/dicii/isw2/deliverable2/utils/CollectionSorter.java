package it.uniroma2.dicii.isw2.deliverable2.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class, used to cmpare object, usually Tickets or Commits, based on a specific object method name
 */
public class CollectionSorter {
    protected static Logger log = LoggerInst.getSingletonInstance();

    private CollectionSorter() {
    }

    /**
     * Sort a <code>List</code> of items, based on a result returned by a method.
     *
     * @param list   list to be sorted
     * @param method method whose return value is used as comparator
     */
    public static void sort(List<?> list, Method method) {
        Collections.sort(list, (Comparator<Object>) (o1, o2) -> {
            try {
                return ((Comparable<Object>) method.invoke(o1)).compareTo(method.invoke(o2));
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.severe(e.getMessage());
            }
            return 0;
        });
    }
}
