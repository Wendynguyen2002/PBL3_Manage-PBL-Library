package com.example.pblManagement.service;

import com.example.pblManagement.model.entities.Account;

public interface CurrentUserService{
    Account getCurrentAccount();

    String getCurrentUserId();
}
