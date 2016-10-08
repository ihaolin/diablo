package me.hao0.diablo.server.dto;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ClientDto {

    private String id;

    private String server;

    private String addr;

    private String uptime;

    public ClientDto() {
    }

    public ClientDto(String id, String server, String addr, String uptime) {
        this.id = id;
        this.server = server;
        this.addr = addr;
        this.uptime = uptime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return "ClientDto{" +
                "id='" + id + '\'' +
                ", server='" + server + '\'' +
                ", addr='" + addr + '\'' +
                ", uptime='" + uptime + '\'' +
                '}';
    }
}
