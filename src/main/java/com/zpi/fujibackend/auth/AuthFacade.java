package com.zpi.fujibackend.auth;

import com.zpi.fujibackend.auth.dto.TokenDto;

public interface AuthFacade {

    TokenDto generateToken(final String googleToken);

    TokenDto generateTokenMock(final String googleToken);

    TokenDto getRefreshToken(final String refreshToken);
}
