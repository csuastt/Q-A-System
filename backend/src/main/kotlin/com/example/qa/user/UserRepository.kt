package com.example.qa.user

import com.example.qa.user.response.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User?, String?>