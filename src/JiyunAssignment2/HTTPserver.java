package JiyunAssignment2;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
public class HTTPserver {
    private String method;

    private String uri;

    private String version;

    private Map<String, String> headers;
    private String message;

    public String getMessage() {
        return message;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    //    private void decodeRequestLine(HTTPsever HTTPsever) {
//        Map<String, String> headers=this.getHeaders();
//        String[] strs = StringUtils.split(reader.readLine(), " ");
//        assert strs.length == 3;
//        HTTPsever.setMethod(strs[0]);
//        HTTPsever.setUri(strs[1]);
//        HTTPsever.setVersion(strs[2]);
//    }
    private static void decodeRequestHeader(BufferedReader reader, HTTPserver HTTPserver) throws IOException {
        // 创建一个 Map 对象，用于存储请求头信息
        Map<String, String> headers = new HashMap<>(16);
        // 读取请求头信息，每行都是一个键值对，以空行结束
        String line = reader.readLine();
        String[] kv;
        while (!"".equals(line)) {
            // 将每行请求头信息按冒号分隔，分别作为键和值存入 Map 中
            kv = StringUtils.split(line, ":");
            assert kv.length == 2;
            headers.put(kv[0].trim(), kv[1].trim());
            line = reader.readLine();
        }
        // 将解析出来的请求头信息存入 Request 对象中
//        HTTPsever.setHeaders(headers);
    }
}
