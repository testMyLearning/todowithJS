package com.example.todo.model.dto.task;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;



public record CreateTaskRequest(
        @NotBlank(message = "Заголовок обязателен")
                @Size(min = 1,max = 100, message = "Длина заголовка от 1 до 100 символов")
        String name,
        @NotBlank(message = "Описание обязательно")
        @Size(min = 1,max = 100, message = "Длина заголовка от 1 до 100 символов")
        String description,

        @NotNull(message = "Дедлайн обязателен")
        LocalDate deadline,

        //@NotNull
        Long userId

) {

}
