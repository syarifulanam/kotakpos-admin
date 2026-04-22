package com.ngulik.kotakpos_admin.exception.error;

public class TokenBlacklistExcception extends RuntimeException {

    public TokenBlacklistExcception(String message) {
        super(message);
    }
}
