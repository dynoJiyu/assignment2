package JiyunAssignment2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

public class RequestMessage{
    private String requestLine;
    private Map<String, String> headers;
    private final String blankline="\n";
    private String requestBody;
    public void setRequestLine(String Func, String sourceAndparameter, String HTTPversion) {
        String requestLine = new String(Func + " /"+ sourceAndparameter + " " + "HTTP/" + HTTPversion +"/n");
        this.requestLine = requestLine;
    }
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public String getBlankline() {
        return blankline;
    }
    public String getRequestBody() {
        return requestBody;
    }
    public String getRequestLine() {
        return requestLine;
    }

//    public String getFunction(){
//        return  requestLine.split(" /")[0];
//    }
//    public String getResource(){
//        return requestLine.split(" /")[1];
//    }
//    public int getLamport(){
//        return Integer.valueOf(headers.get("Lamport-Clock"));
//    }
    public void setRequestLine(String requestLine) {
        this.requestLine = requestLine;
    }
}