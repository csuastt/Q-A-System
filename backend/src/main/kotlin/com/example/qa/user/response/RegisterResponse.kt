package com.example.qa.user.response

import lombok.Data
import lombok.RequiredArgsConstructor

@Data
@RequiredArgsConstructor
class RegisterResponse(var token: String, var user: User)