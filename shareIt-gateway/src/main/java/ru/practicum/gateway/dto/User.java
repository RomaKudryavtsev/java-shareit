package ru.practicum.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
public class User {
    Long id;
    String name;
    @Email
    String email;
}
