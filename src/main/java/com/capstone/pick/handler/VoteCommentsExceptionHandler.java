package com.capstone.pick.handler;

import com.capstone.pick.controller.VoteCommentsController;
import com.capstone.pick.exeption.UserMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = VoteCommentsController.class)
public class VoteCommentsExceptionHandler {

    @ExceptionHandler(UserMismatchException.class)
    public ResponseEntity<ErrorResponse> UserMismatchException() {
        ErrorResponse errorResponse = new ErrorResponse(403, "해당 댓글을 작성한 유저가 아닙니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
