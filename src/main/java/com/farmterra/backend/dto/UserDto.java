package com.farmterra.backend.dto;

import com.farmterra.backend.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private User.UserRole role;
    private String phone;
    private String address;
    private String avatar;
    private String farmName;
    private String farmDescription;
}
