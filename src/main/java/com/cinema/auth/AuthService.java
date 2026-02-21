
package com.cinema.auth;

import com.cinema.common.dto.LoginRequestDTO;
import com.cinema.common.dto.RegisterRequestDTO;

public interface AuthService {
    void register(RegisterRequestDTO dto);
    String login(LoginRequestDTO dto);
}
