package me.hao0.diablo.server.context;

import me.hao0.diablo.server.model.ClientSession;

public final class ClientContext {

	private ClientContext(){}

	private static ThreadLocal<ClientSession> loginInfo = new ThreadLocal<>();

	public static void set(ClientSession info){
		loginInfo.set(info);
	}

	public static ClientSession get(){
		return loginInfo.get() == null ? null : loginInfo.get();
	}

	public static boolean isLogin(){
		return loginInfo.get() != null;
	}

	public static void clear(){
		loginInfo.remove();
	}
}