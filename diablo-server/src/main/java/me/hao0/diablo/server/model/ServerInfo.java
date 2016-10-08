package me.hao0.diablo.server.model;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ServerInfo {

    /**
     * IP:PORT
     */
    private String server;

    /**
     * Server Status
     */
    private Integer status;

    /**
     * Server status desc
     */
    private String statusDesc;

    /**
     * Server is leader or not
     */
    private Boolean isLeader;

    /**
     * Server's current client count
     */
    private Integer clientCount;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public Boolean getLeader() {
        return isLeader;
    }

    public void setLeader(Boolean leader) {
        isLeader = leader;
    }

    public Integer getClientCount() {
        return clientCount;
    }

    public void setClientCount(Integer clientCount) {
        this.clientCount = clientCount;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "server='" + server + '\'' +
                ", status=" + status +
                ", statusDesc='" + statusDesc + '\'' +
                ", isLeader=" + isLeader +
                ", clientCount=" + clientCount +
                '}';
    }
}
