package club.codermax.rpc.framework;

import java.io.Serializable;

public class RpcResponse implements Serializable {

    private String responseId;
    private int status;
    private Object data;

    public RpcResponse() {
        super();
        this.responseId = responseId;
        this.status = status;
        this.data = data;
    }


    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
