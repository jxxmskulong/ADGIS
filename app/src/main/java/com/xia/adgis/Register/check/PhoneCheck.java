package com.xia.adgis.Register.check;

/**
 *
 * Created by xiati on 2017/12/9.
 */
import java.util.ArrayList;
import java.util.List;

public class PhoneCheck {
    private static List<String> list = new ArrayList<String>();
    static {
        list.add("133");
        list.add("139");
        list.add("138");
        list.add("137");
        list.add("136");
        list.add("135");
        list.add("134");
        list.add("159");
        list.add("158");
        list.add("157");
        list.add("152");
        list.add("151");
        list.add("150");
        list.add("188");
        list.add("187");
        list.add("130");
        list.add("131");
        list.add("132");
        list.add("155");
        list.add("156");
        list.add("186");
        list.add("185");
        list.add("133");
        list.add("153");
        list.add("180");
        list.add("181");
        list.add("182");
        list.add("183");
        list.add("184");
        list.add("186");
        list.add("187");
        list.add("189");
    }
    public static boolean checkNumber(String num){
        return list.contains(num);
    }
}
