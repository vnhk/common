package com.bervan.common.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class CustomSpringErrorController implements ErrorController {

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {
        Object uriAttr = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String uri = uriAttr != null ? uriAttr.toString() : "";
        String accept = request.getHeader("Accept");

        if (uri.startsWith("/api") || (accept != null && accept.contains("application/json"))) {
            Object statusAttr = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            int status = statusAttr instanceof Integer ? (Integer) statusAttr : 500;
            Object messageAttr = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            String message = messageAttr != null && !messageAttr.toString().isBlank()
                    ? messageAttr.toString() : "An error occurred";
            return ResponseEntity.status(status).body(Map.of("status", status, "error", message));
        }

        return "redirect:/application-error";
    }
}
