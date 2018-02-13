package com.lmtri.sharespace.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmtri on 8/13/2017.
 */

public class ListHelper {
    public static <T> ArrayList<Integer> compare(List<T> list, List<T> anotherList) {
        ArrayList<Integer> differentIndexes = new ArrayList<>();
        for (int i = 0; i < Math.min(list.size(), anotherList.size()); ++i) {
            if (!list.get(i).equals(anotherList.get(i))) {
                differentIndexes.add(i);
            }
        }
        return differentIndexes;
    }
}
