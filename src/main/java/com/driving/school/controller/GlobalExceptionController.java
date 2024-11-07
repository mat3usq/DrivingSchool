package com.driving.school.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionController {

    @ExceptionHandler({Exception.class})
    public ModelAndView exceptionHandler(Exception exception, HttpServletRequest request) {
        String errorMsg;
        ModelAndView errorPage = new ModelAndView("error/error");

        if (exception.getMessage() != null)
            errorMsg = exception.getMessage();
        else if (exception.getCause() != null)
            errorMsg = exception.getCause().toString();
        else
            errorMsg = exception.toString();

        errorPage.addObject("errorMsg", errorMsg);

        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String status;
        if (statusObj != null) {
            status = statusObj.toString();
        } else {
            status = "500";
        }

        errorPage.addObject("status", status);

        log.error("Błąd status: {}, wiadomość: {}", status, errorMsg, exception);

        return errorPage;
    }
}
