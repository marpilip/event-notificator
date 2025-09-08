package event.notificator.jwt;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
        @NotBlank
        String login,

        @NotBlank
        String password) {
}
