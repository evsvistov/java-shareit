package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Поле 'available' должно быть указано")
    private Boolean available;
    private Long requestId;

    public ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
