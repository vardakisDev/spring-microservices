package com.interview.userservice.application.usecase.command;

public record CreateUserCommand(String email, String firstName, String lastName, boolean vip) {
}