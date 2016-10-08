package me.hao0.diablo.server.api;

import me.hao0.diablo.common.model.JsonResponse;
import me.hao0.diablo.common.util.JsonUtil;
import me.hao0.diablo.server.util.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Controller
public class TowerIndexes extends AbstractErrorController {

    @Autowired
    public TowerIndexes(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping(value = {"/", "/index", "/index.html"})
    public String index(){
        return "index";
    }

    @RequestMapping("/error")
    public String error(HttpServletRequest request, HttpServletResponse response){

        HttpStatus status = super.getStatus(request);

        if (status == HttpStatus.NOT_FOUND){
            return "index";
        }

        if (status == HttpStatus.FORBIDDEN){
            Responses.writeJson(response, JsonUtil.INSTANCE.toJson(JsonResponse.AUTH_FAIL));
            return "";
        }

        // default index
        return "index";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
