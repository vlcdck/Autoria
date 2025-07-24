package com.autoria.services.security;

import com.autoria.models.user.dto.ChangeAccountTypeDto;

public interface AccountTypeService {
    void changeAccountType(ChangeAccountTypeDto changeAccountTypeDto);
}
