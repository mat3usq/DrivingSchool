package com.driving.school.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        ErrorAttributeOptions options = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.STACK_TRACE
        );

        Map<String, Object> errorAttributesMap = this.errorAttributes.getErrorAttributes(
                new ServletWebRequest(request), options
        );

        Integer statusCode = (Integer) errorAttributesMap.get("status");
        String errorMsg = (String) errorAttributesMap.get("message");
        String requestUri = (String) errorAttributesMap.get("path");
        String stackTrace = (String) errorAttributesMap.get("trace");
        String exception = (String) errorAttributesMap.get("exception");
        String servletName = (String) request.getAttribute("jakarta.servlet.error.servlet_name");

        ModelAndView errorPage = new ModelAndView("error/error");
        errorPage.addObject("status", statusCode);
        errorPage.addObject("errorMsg", errorMsg);
        errorPage.addObject("requestUri", requestUri);
        errorPage.addObject("stackTrace", stackTrace);
        errorPage.addObject("exception", exception);
        errorPage.addObject("servletName", servletName);

        if (exception != null) {
            log.error("Error status: {}, message: {}, exception: {}", statusCode, errorMsg, exception);
        } else {
            log.error("Error status: {}, message: {}", statusCode, errorMsg);
        }

        return errorPage;
    }
}
