package com.phyex.animecixnotifier.dto;


import java.util.List;

public record SessionInfo(String email, List<String> cookie, Integer maxAge) {

}
