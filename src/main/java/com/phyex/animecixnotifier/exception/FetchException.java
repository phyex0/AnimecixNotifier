package com.phyex.animecixnotifier.exception;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FetchException extends RuntimeException {

    public FetchException(String message) {
        super(message);
    }
}
