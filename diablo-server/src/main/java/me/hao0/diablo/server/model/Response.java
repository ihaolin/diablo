package me.hao0.diablo.server.model;

import com.google.common.base.Objects;
import java.io.Serializable;

/**
 * Service Response Wrapper
 */
public final class Response<T> implements Serializable {

    private static final long serialVersionUID = 3727205004706510648L;

    public static final Integer OK = 200;

    /**
     * 500
     */
    public static final Integer ERR = 500;

    /**
     * status
     */
    private Integer status;

    /**
     * error message
     */
    private String err;

    /**
     * data
     */
    private T data;

    public static <T> Response<T> ok(){
        Response r = new Response();
        r.status = OK;
        return r;
    }

    public static <T> Response<T> ok(Object data){
        Response r = new Response();
        r.status = OK;
        r.data = data;
        return r;
    }

    public static <T> Response<T> notOk(String err){
        Response r = new Response();
        r.status = ERR;
        r.err = err;
        return r;
    }

    public static <T> Response<T> notOk(Integer status, String err){
        Response r = new Response();
        r.status = status;
        r.err = err;
        return r;
    }

    public Boolean isSuccess(){
        return Objects.equal(status, OK);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        status = OK;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", err='" + err + '\'' +
                ", data=" + data +
                '}';
    }
}