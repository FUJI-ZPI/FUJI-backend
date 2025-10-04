package com.zpi.fujibackend.user;

import java.util.UUID;

public interface UserFacade {

    UUID findOrCreateByEmail(final String email);
}
