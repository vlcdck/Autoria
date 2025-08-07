package com.autoria.exeptions;


public class UserBannedException extends RuntimeException {
    public UserBannedException(String message) {
        super(message);
    }
}