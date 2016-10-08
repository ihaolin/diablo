package me.hao0.diablo.server.interceptor;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import me.hao0.diablo.common.model.JsonResponse;
import me.hao0.diablo.common.util.Constants;
import me.hao0.diablo.common.util.ClientUris;
import me.hao0.diablo.server.context.ClientContext;
import me.hao0.diablo.server.model.ClientSession;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.ClientService;
import me.hao0.diablo.server.util.Logs;
import me.hao0.diablo.server.util.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ClientInterceptor extends HandlerInterceptorAdapter {

    @Value("${diablo.clientAuth:true}")
    private Boolean clientAuth;

    @Autowired
    private ClientService clientService;

    private final Set<String> ignoreUrls = Sets.newHashSet(
        ClientUris.CLIENT_API + ClientUris.SERVER_ROUTE,
        ClientUris.CLIENT_API + ClientUris.REGISTER
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        if (!uri.startsWith(ClientUris.CLIENT_API)
                || ignoreUrls.contains(uri)){
            return true;
        }

        // get client info
        String clientId = request.getHeader(Constants.CLIENT_ID_HEADER);
        Response<ClientSession> clientResp =  clientService.getSession(clientId);
        if (!clientResp.isSuccess()){
            Logs.error("failed to find client info(clientId={}, uri={}), cause: {}", clientId, uri, clientResp.getErr());
            Responses.writeText(response, JsonResponse.SERVER_ERR.getStatus());
            return false;
        }
        ClientSession clientSession = clientResp.getData();
        if (clientSession == null){
            Logs.error("client info is missing(clientId={}, uri={}), client session is expired", clientId, uri);
            Responses.writeText(response, JsonResponse.NEED_LOGIN.getStatus());
            return false;
        }

        // validate client key if necessary
        if (clientAuth){
            String appKey = request.getHeader("appKey");
            if (!Objects.equal(clientSession.getAppKey(), appKey)){
                Logs.error("failed to auth client({}): key isn't right", clientId);
                Responses.writeText(response, JsonResponse.AUTH_FAIL.getStatus());
                return false;
            }
        }

        // set client context info
        ClientContext.set(clientSession);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ClientContext.clear();
    }
}
