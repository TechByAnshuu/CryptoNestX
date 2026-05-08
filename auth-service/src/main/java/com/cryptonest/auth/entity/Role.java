package com.cryptonest.auth.entity;

/**
 * Authorization roles.
 * Extend here (e.g. MODERATOR) — SecurityConfig.java is the only other touch-point.
 */
public enum Role {
    USER,
    ADMIN
}
