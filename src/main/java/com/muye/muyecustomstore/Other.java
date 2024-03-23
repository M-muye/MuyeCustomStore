package com.muye.muyecustomstore;

import java.util.ArrayList;
import java.util.List;

public class Other {
    public static String Replace(String string){
        return string.replace("&","§");
    }
    public static List<String> ReplaceList(List<String> strings){
        strings.replaceAll(s -> s.replace("&", "§"));
        return strings;
    }
    public static List<Integer> LoadSlot(String slots){
        List<Integer> slotList = new ArrayList<>();
        String[] slotRanges = slots.split(",");
        for (String slotRange : slotRanges) {
            if (slotRange.contains("-")) {
                String[] rangeBounds = slotRange.split("-");
                int start = Integer.parseInt(rangeBounds[0]);
                int end = Integer.parseInt(rangeBounds[1]);
                for (int i = start; i <= end; i++) {
                    slotList.add(i);
                }
            } else {
                int slot = Integer.parseInt(slotRange);
                slotList.add(slot);
            }
        }
        return slotList;
    }
    public static boolean canParseInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean canParseDou(String s){
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
