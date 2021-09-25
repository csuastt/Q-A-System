package com.example.qa.user

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import com.example.qa.user.UserRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import com.example.qa.user.response.RegisterResponse
import com.example.qa.user.response.User

@RestController
@RequestMapping("/api/user")
class UserController(private val repository: UserRepository) {
    @PostMapping("/register")
    fun userRegistration(@RequestBody newUser: User): RegisterResponse {
        repository.save(newUser)
        return RegisterResponse("1", newUser)
    }
}