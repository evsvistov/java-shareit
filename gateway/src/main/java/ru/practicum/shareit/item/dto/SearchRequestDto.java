package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {

    @NotBlank(message = "Строка поиска не может быть пустая")
    private String text;
}