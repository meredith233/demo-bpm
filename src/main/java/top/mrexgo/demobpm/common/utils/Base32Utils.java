package top.mrexgo.demobpm.common.utils;

import cn.hutool.core.codec.Base32;

/**
 * @author liangjuhong
 * @since 2021/5/7 - 16:41
 */
public class Base32Utils {

    public static String base32ToString(String s) {
        return Base32.encode(s);
    }

    public static String base32Decode(String str) {
        return Base32.decodeStr(str);
    }


}
