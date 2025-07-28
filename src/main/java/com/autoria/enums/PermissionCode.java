package com.autoria.enums;

public enum PermissionCode {
    // Own ads
    CREATE_OWN_AD,
    VIEW_OWN_AD,
    UPDATE_OWN_AD,
    DELETE_OWN_AD,

    // Any ads (manager/admin)
    VIEW_ANY_AD,
    UPDATE_ANY_AD,
    DELETE_ANY_AD,

    // Other permissions
    VIEW_AD_STATISTICS,
    VIEW_AD_AVERAGE_PRICE,
    MANAGE_ADS,
    APPROVE_AD,
    BAN_USER,
    MANAGE_DEALERSHIP,
    MANAGE_ROLES,
    MANAGE_PERMISSIONS,
    VIEW_ALL_USERS,
}

