
package com.cinema.auth;

import com.cinema.common.dto.LoginRequestDTO;
import com.cinema.common.dto.RegisterRequestDTO;

public interface AuthService {
    String register(RegisterRequestDTO dto);
    String login(LoginRequestDTO dto);
}
