package com.phyex.animecixnotifier.dto;


import com.phyex.animecixnotifier.enums.NotifyType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(@NotBlank @Email String email, @NotBlank String password,
                          @NotNull NotifyType notifyType,
                          @NotBlank String notifyId) {
}
