package event.notificator.dto;

public record FieldChangeDto (
        Object oldField,
        Object newField
) {
}
