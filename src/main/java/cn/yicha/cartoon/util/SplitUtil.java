package cn.yicha.cartoon.util;

/**
 * lucene去噪类，替换lucene的特殊字符
 * @author xrx
 * @since 2012-09-18
 */
public class SplitUtil {
    private static final String LuceneSpecChars = ".+-#!&|(){}[]^\"~$*?:\\";

    /**
     * 对关键词去噪
     * @param key
     * @param removeSpace 是否移除空格
     * @return
     */
    public static String escapeKeyword(String key,boolean removeSpace) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < key.length(); i++) {
            char kCh = key.charAt(i);
            if(removeSpace && kCh==' '){
                continue;
            }
            sb.append(getEscapedChar(kCh));
        }
        return sb.toString();
    }

    /**
     * 去噪，不去空格
     * @param key
     * @return
     */
    public static String escapeKeyword(String key){
        return escapeKeyword(key,false);
    }

    /**
     * 对单个词去噪
     * @param ch
     * @return
     */
    private static String getEscapedChar(char ch) {
        for (int j = 0; j < LuceneSpecChars.length(); j++) {
            char es = LuceneSpecChars.charAt(j);
            if (ch == es)
                return "\\" + ch;
        }
        return ch + "";
    }
}


