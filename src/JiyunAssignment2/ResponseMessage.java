package JiyunAssignment2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.Map;

public class ResponseMessage {
    private String statusLine;
    private Map<String, String> headers;
    private final String blankline="\n";
    private String responseBody;
    private static final Map<Integer, String> statusDic = new HashMap<>();
    static {
        statusDic.put(201, "HTTP_CREATED");
        statusDic.put(200, "UPLOAD_SUCCESS");
        statusDic.put(400, "OTHER_REQUEST");
        statusDic.put(204, "NO_CONTENT");
        statusDic.put(500, "INTERNAL_SERVER_ERROR");
    }

    public String getBlankline() {
        return blankline;
    }
    @JSONField(serialize = false)
    public Map<Integer, String> getStatusDic() {
        return statusDic;
    }
    public String getResponseBody() {
        return responseBody;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public String getStatusLine() {
        return statusLine;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
    public void setStatusLine(String statusLine)
    {
        this.statusLine=statusLine;
    }
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    public void setStatusLine(double HTTPversion, int statu) {
        String statusLine = new String("HTTP/" + String.valueOf(HTTPversion) + " " +statu+ " " +statusDic.get(statu)  + "\n");
        this.statusLine = statusLine;
    }

    public static ResponseMessage StringToRM(String message){
        return JSON.parseObject(message, ResponseMessage.class);
    }

}
