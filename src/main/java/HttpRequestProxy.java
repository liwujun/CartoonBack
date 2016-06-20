

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: yicha
 * Date: 11-12-7
 * Time: 下午6:33
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequestProxy {

    /**
     * 连接超时
     */
    private static int connectTimeOut = 5000;

    /**
     * 读取数据超时
     */
    private static int readTimeOut = 10000;

    /**
     * 请求编码
     */
    private static String requestEncoding = "UTF-8";


    /**
     * <pre>
     * 发送带参数的POST的HTTP请求
     * </pre>
     *
     * @param reqUrl     HTTP请求URL
     * @param parameters 参数映射表
     * @return HTTP响应的字符串
     */
    public static String doPost(String reqUrl, Map parameters,
                                String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            StringBuffer params = new StringBuffer();
            for (Iterator iter = parameters.entrySet().iterator(); iter
                    .hasNext(); ) {
                Map.Entry element = (Map.Entry) iter.next();
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(element.getValue().toString(),
                        recvEncoding));
                params.append("&");
            }

            if (params.length() > 0) {
                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("POST");
            System.setProperty("sun.net.client.defaultConnectTimeout", String
                    .valueOf(HttpRequestProxy.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
            System.setProperty("sun.net.client.defaultReadTimeout", String
                    .valueOf(HttpRequestProxy.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
            // url_con.setConnectTimeout(5000);//（单位：毫秒）jdk
            // 1.5换成这个,连接超时
            // url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in,
                    recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer tempStr = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            while (tempLine != null) {
                tempStr.append(tempLine);
                tempStr.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = tempStr.toString();
            rd.close();
            in.close();
        } catch (IOException e) {

        } finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }
        return responseContent;
    }

    public static String doPost1(String reqUrl, String params,
                                 String recvEncoding) {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try {
            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("POST");
            System.setProperty("sun.net.client.defaultConnectTimeout", String
                    .valueOf(HttpRequestProxy.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
            System.setProperty("sun.net.client.defaultReadTimeout", String
                    .valueOf(HttpRequestProxy.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
            // url_con.setConnectTimeout(5000);//（单位：毫秒）jdk
            // 1.5换成这个,连接超时
            // url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);
            byte[] b = params.getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in,
                    recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer tempStr = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            while (tempLine != null) {
                tempStr.append(tempLine);
                tempStr.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = tempStr.toString();
            rd.close();
            in.close();
        } catch (IOException e) {

        } finally {
            if (url_con != null) {
                url_con.disconnect();
            }
        }
        return responseContent;
    }


    /**
     * @return 连接超时(毫秒)
     * @see
     */
    public static int getConnectTimeOut() {
        return HttpRequestProxy.connectTimeOut;
    }

    /**
     * @return 读取数据超时(毫秒)
     * @see
     */
    public static int getReadTimeOut() {
        return HttpRequestProxy.readTimeOut;
    }

    /**
     * @return 请求编码
     * @see
     */
    public static String getRequestEncoding() {
        return requestEncoding;
    }

    /**
     * @param connectTimeOut 连接超时(毫秒)
     * @see
     */
    public static void setConnectTimeOut(int connectTimeOut) {
        HttpRequestProxy.connectTimeOut = connectTimeOut;
    }

    /**
     * @param readTimeOut 读取数据超时(毫秒)
     * @see
     */
    public static void setReadTimeOut(int readTimeOut) {
        HttpRequestProxy.readTimeOut = readTimeOut;
    }

    /**
     * @param requestEncoding 请求编码
     * @see
     */
    public static void setRequestEncoding(String requestEncoding) {
        HttpRequestProxy.requestEncoding = requestEncoding;
    }

    public static void main(String[] args) {

//        for (int i = 1; i <=100; i++) {
//            if (i % 2 == 0) {   //0 2 4 6 8
//                Map map = new HashMap();
//                map.put("type", "click");
//                map.put("value", i);
//                String temp = HttpRequestProxy.doPost("http://localhost:8080/updateClickNum", map, "UTF-8");
//                System.out.println("返回的消息是:" + temp);
//            }
//            Map map = new HashMap();
//            map.put("type", "show");
//            map.put("value", new Random().nextInt(100)+"#"+new Random().nextInt(100)+"#"+new Random().nextInt(100)+"#"+new Random().nextInt(100));
//            String temp = HttpRequestProxy.doPost("http://localhost:8080/updateClickNum", map, "UTF-8");
//            System.out.println("返回的消息是:" + temp);
//        }

        HttpRequestProxy hrp = new HttpRequestProxy();
//        testPutResourceData(hrp);
        testInitSoftData(hrp);

//        testDelResourceData(hrp);

//        testUpdateResourceData(hrp);

//        try {
//            testGetTopData(hrp);
//        	testGetResetTopData(hrp);
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

//        testUpdateClickNum(hrp);

//        testGetResetTopData(hrp);


//        for(int i=1;i<=3160;i++){
//            String str = "http://localhost:8080/updateClickNum?type=show&value="+i+"\r\n";
//            writeToFile("d:/clickPath.txt",str);
//        }

//        try {
//            readFile("d:/top/clickPath.txt");
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
    }

    public static void testGetResetTopData(HttpRequestProxy hrp) {
        String content = "<params><param><id>2</id><handleNum>16</handleNum><time>2011-12-23 00:00:00</time></param></params>";
        Map map = new HashMap();
        map.put("operatorType", "select");
//         map.put("operatorType", "update");

        map.put("product_type", "小说");
        map.put("pageSize", 20);
        map.put("pageNum", 0);
////
//        map.put("tit", "言情");
//
                map.put("gender","c") ;
        map.put("time_type", "1");


//        map.put("is_full", "1");
        map.put("is_new", "0");

//        map.put("content",content);
        String result = HttpRequestProxy.doPost("http://localhost:8080/resetTop/resetTop", map, "UTF-8");
        System.out.print(result);
    }

    public static void testGetTopData(HttpRequestProxy hrp) throws UnsupportedEncodingException {
        Map map = new HashMap();
        map.put("ptp", "软件");
        map.put("ps", 20);
//        map.put("needImage", true);
        map.put("pno", 0);
//        map.put("sex", "c");
        map.put("time", "all"); 
//        map.put("isn", "false");//getTopData?ptp=小说&ps=20&pno=0&sex=c&time=d&isn=false
//        http://211.100.45.15:18052/getTopData?ptp=%E8%BD%AF%E4%BB%B6&mt=&stp=&time=m&ps=6&pno=0 
        	String result = HttpRequestProxy.doPost("http://localhost:8080/resetTop/getTopData", map, "UTF-8");
        System.out.print(result);

        //榜单列表
//        map.put("isf","true");   //连载，全本
//        map.put("time", "m");    //日，周，月，总 , 连载 , 全本
        /*    String[] types = new String[]{"仙侠", "侦探", "军事", "历史", "同人", "奇幻", "悬疑", "文学", "武侠",
                "玄幻", "社科", "科幻", "穿越", "竞技", "纪实", "网游", "美文", "职场", "言情", "都市", "青春"};
        String[] params = new String[]{"time=d", "time=w", "time=m", "time=all", "isf=true", "isf=false"};

        String reqUrl = "http://localhost:8080/getTopData";
        for (String type : types) {
            for (String param : params) {
                StringBuffer sb = new StringBuffer();
                sb.append("ptp=").append(URLEncoder.encode("小说", "UTF-8")).append("&")
                        .append("tit=").append(URLEncoder.encode(type, "UTF-8")).append("&").append(param);
                String path = reqUrl + "?" + sb.toString();
                sb.append("&pno=0&ps=3160");

                String result = HttpRequestProxy.doPost1(reqUrl, sb.toString(), "utf-8");
                System.out.println(result);
                try {
                    readXmlByDom4j(new ByteArrayInputStream(result.getBytes("utf-8")), path);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

        }*/


        //排行榜
//        -综合|男频|女频
//        -热书榜.新书榜.连载.全本
//        -【日|周|月|总】
        /*  map.put("sex","m");//f,m,c  男频，女频，综合
//        map.put("isn","true"); //新书  true,false
        map.put("isf","false");  //连载，全本 true,false
        map.put("time","d");    //日，周，月，总   d,w,m,all
        String result = HttpRequestProxy.doPost("http://localhost:8080/getTopData", map, "UTF-8");
        System.out.print(result);*/

//         String result = HttpRequestProxy.doPost("http://211.100.45.15:18052/getTopData", map, "UTF-8");
        //排行榜
//        map.put("sex","c") ;
//        map.put("isn","false");

//        String result = HttpRequestProxy.doPost("http://localhost:8080/getTopData", map, "UTF-8");
//        System.out.print(result);

//        String[] sex = new String[]{"sex=f", "sex=m", "sex=c"};
//        String[] isn = new String[]{"isn=true", "isn=false", "isf=true", "isf=false"};
//        String[] time = new String[]{"time=d", "time=w", "time=m", "time=all"};
//        String reqUrl1 = "http://localhost:8080/getTopData";
//        for (String s : sex) {
//            for (String ne : isn) {
//                for (String t : time) {
//                    StringBuffer sb = new StringBuffer();
//                    sb.append("ptp=").append(URLEncoder.encode("小说", "UTF-8")).append("&")
//                            .append(s).append("&").append(ne).append("&").append(t);
//                    String path = reqUrl1 + "?" + sb.toString();
//                    sb.append("&pno=0&ps=3160");
//
//                    String result = HttpRequestProxy.doPost1(reqUrl1, sb.toString(), "utf-8");
//                    System.out.println(result);
//                    try {
//                        readXmlByDom4j(new ByteArrayInputStream(result.getBytes("utf-8")), path);
//                    } catch (Exception e) {
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                    }
//                }
//            }
//        }
    }

    public static void testUpdateResourceData(HttpRequestProxy hrp) {
        Map map = new HashMap();
        map.put("id", 30);
        map.put("resource_name", "test2");
//        map.put("release_time","2011-12-15 00:11:00");
        String result = HttpRequestProxy.doPost("http://localhost:8080/updateResourceData", map, "UTF-8");
        System.out.print(result);
    }

    public static void testDelResourceData(HttpRequestProxy hrp) {
        Map map = new HashMap();
        map.put("id", 30);
        String result = HttpRequestProxy.doPost("http://localhost:8080/delResourceData", map, "UTF-8");
        System.out.print(result);
    }

    public static void testPutResourceData(HttpRequestProxy hrp) {
        String xml = hrp.readXml("resource.xml");
        Map map = new HashMap();
        map.put("content", xml);
//        String result = HttpRequestProxy.doPost("http://211.100.45.15:18052/putResourceData", map, "UTF-8");
        String result = HttpRequestProxy.doPost("http://localhost:8080/putResourceData", map, "UTF-8");
        System.out.print(result);
    }

    public static void testInitSoftData(HttpRequestProxy hrp) {
        String xml = hrp.readXml("soft.xml");
        Map map = new HashMap();
        map.put("content", xml);
//        String result = HttpRequestProxy.doPost("http://211.100.45.15:18052/putResourceData", map, "UTF-8");
        String result = HttpRequestProxy.doPost("http://localhost:8080/InitSoftData", map, "UTF-8");
        System.out.print(result);
    }
    public static int randonNum(int n) {
        int num = new Random().nextInt(n);
        if (num == 0) {
            num = num + 1;
        } else if (num == 3159) {
            num = num + 1;
        }
        return num;
    }

    public static void testUpdateClickNum(HttpRequestProxy hrp) {

        for (int i = 0; i <= 10000; i++) {
            Map map1 = new HashMap();
            map1.put("type", "show");
            map1.put("value", randonNum(3160) + "|" + randonNum(3160) + "|" + randonNum(3160) + "|" + randonNum(3160)
                    + "|" + randonNum(3160) + "|" + randonNum(3160) + "|" + randonNum(3160) + "|" + randonNum(3160) + "|" +
                    randonNum(3160) + "|" + randonNum(3160));
            String temp1 = HttpRequestProxy.doPost("http://localhost:8080/updateClickNum", map1, "UTF-8");

            Map map = new HashMap();
            map.put("type", "click");
            map.put("value", randonNum(3160));

            String temp = HttpRequestProxy.doPost("http://localhost:8080/updateClickNum", map, "UTF-8");


        }

//        String temp = HttpRequestProxy.doPost("http://localhost:8080/updateClickNum", map, "UTF-8");
    }

    public String readXml(String fileName) {
    	if(fileName == "resource.xml") {
    		return "<?xml version='1.0' encoding='utf-8'?><resources><resource><resource_name>药窕淑女</resource_name><resource_type>言情</resource_type><author>琴律</author> <gender>f</gender><is_full>0</is_full><release_time>2011-12-14 00:00:00</release_time><product_type>小说</product_type> <brand>a</brand><system_type>a</system_type><model>a</model></resource></resources>";
    	}
    		//    	 InputStream inputStream = this.getClass().getResourceAsStream(fileName);
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        String result = null;
        byte[] buf = new byte[1024];
        ByteArrayOutputStream bot = new ByteArrayOutputStream();
        int len = 0;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                bot.write(buf, 0, len);
            }

            result = new String(bot.toByteArray(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;
    }
    //  <records totalcount='156'><record><id>2792</id><resource_name>腹黑公主的绝版王子</resource_name><resource_type>青春</resource_type><author>自行车小姐</author><total_click_num>24</total_click_num></record>

    public static void readXmlByDom4j(InputStream inputStream, String path) throws DocumentException {
        StringBuffer sb = new StringBuffer();
        String clickPath = "http://localhost:8080/updateClickNum?type=show&value=";
        StringBuffer sbRequest = new StringBuffer();
        int ps = 20;
        int pno = 0;
        sbRequest.append(path).append("&pno=").append(pno).append("&ps=").append(ps);
        writeToFile("d:/getdata.txt", sbRequest.toString() + "\r\n");

        SAXReader reader = new SAXReader();
        int type = 0;
        if (inputStream != null) {
            org.dom4j.Document document = reader.read(inputStream);
            org.dom4j.Element root = document.getRootElement();
            int i = 0;
            for (Iterator it = root.elementIterator("record"); it.hasNext(); ) {
                org.dom4j.Element el = (org.dom4j.Element) it.next();
                String resource_id = el.elementText("id");
                if (resource_id != null && !"".equals(resource_id)) {
                    if (i < ps) {
                        sb.append(resource_id).append("|");
                        i++;
                    } else {
                        sb.deleteCharAt(sb.length() - 1);
                        sbRequest = new StringBuffer();
                        sbRequest.append(path).append("&pno=").append(++pno).append("&ps=").append(ps);
                        writeToFile("d:/getdata.txt", sbRequest.toString() + "\r\n");
                        writeToFile("d:/show.txt", clickPath + sb.toString() + "\r\n");
                        sb = new StringBuffer();
                        sb.append(resource_id).append("|");
                        i = 1;
                    }

                }
            }
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            writeToFile("d:/show.txt", clickPath + sb.toString() + "\r\n");
        }
    }

    public static void writeToFile(String fileName, String str) {
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName, true);
            outputStream.write(str.getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public static void readFile(String fileName) throws IOException {
        FileInputStream inputStream = new FileInputStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;

        while ((line = br.readLine()) != null) {
            sendGet(line,null);
//            System.out.println(line);
        }
    }


    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是name1=value1&name2=value2的形式。
     * @return URL所代表远程资源的响应
     */
    public static String sendGet(String url,String param) {
        String result = "";
        BufferedReader in = null;
        try {
//            String urlName = url + "?" + param;
//            URL realUrl = new URL(urlName);

             URL realUrl = new URL(url);
//打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
//设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//建立实际的连接
            conn.connect();
//获取所有响应头字段
            Map<String,List<String>>  map = conn.getHeaderFields();
//遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->;" + map.get(key));
            }
//定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
//使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
