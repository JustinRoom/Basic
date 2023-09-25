package jsc.org.lib.basic.utils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class ListUtils {

    @NonNull
    public static <T>List<T> toColumnList(@NonNull List<T> rowList, int columnCount){
        int len = rowList.size();
        int avg = len / columnCount;
        int rest = len % columnCount;
        int[] columnValues = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnValues[i] = avg;
            if (rest > 0) {
                columnValues[i] += 1;
                rest--;
            }
        }
        List<T> newList = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            newList.add(rowList.get(getRealPosition(columnValues, columnCount, i)));
        }
        return newList;
    }

    private static int getRealPosition(int[] columnValues, int columnCount, int pos) {
        int rowIndex = pos / columnCount;
        int columnIndex = pos % columnCount;
        int index = 0;
        for (int i = 0; i < columnIndex; i++) {
            index += columnValues[i];
        }
        index += rowIndex;
        return index;
    }
}
