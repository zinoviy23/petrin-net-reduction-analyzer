package hse.se.aaizmaylov.petrinetslibrary.utils;

import java.util.Collection;

public class CollectionsUtils {
    public static <T> T first(Collection<? extends T> set) {
        return set.iterator().next();
    }
}