package com.interview.userservice.application.usecase.command;

public record UpdateUserCommand(String email, String firstName, String lastName, boolean vip) {
}