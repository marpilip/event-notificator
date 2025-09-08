package event.notificator.model;

public record User(
        Long id,
        String login,
        Role role
) {
}
