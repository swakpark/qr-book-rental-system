package com.example.library.exception;

import com.example.library.controller.qr.QrBookController;
import com.example.library.controller.qr.QrLoanController;
import com.example.library.controller.admin.AdminBookViewController;
import org.springframework.http.HttpStatus;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(assignableTypes = {
        AdminBookViewController.class,
        QrBookController.class,
        QrLoanController.class
})
public class ViewExceptionHandler {

    /* 책 없음 */
    @ExceptionHandler(BookNotFoundException.class)
    public ModelAndView handleBookNotFound(BookNotFoundException e) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", e.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    /* 연체로 인한 대여 제한 */
    @ExceptionHandler(OverdueRestrictedException.class)
    public ModelAndView handleOverdue(OverdueRestrictedException e) {

        ModelAndView mav = new ModelAndView("qr-result");
        mav.addObject("errorTitle", "대여 제한 안내");
        mav.addObject(
                "errorMessage",
                "연체 " + e.getOverdueDays() + "일로 인해 대여가 제한되었습니다.\n" +
                        "연체료: " + e.getPenaltyAmount() + "원"
        );
        mav.addObject("showAdminLink", true);
        mav.setStatus(HttpStatus.FORBIDDEN);

        return mav;
    }

    /* 기타 예외 */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleOther(Exception e) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject(
                "errorMessage",
                "예상치 못한 오류가 발생했습니다: " + e.getMessage()
        );
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}
