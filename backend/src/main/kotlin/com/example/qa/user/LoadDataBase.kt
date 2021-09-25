package com.example.qa.user

import com.example.qa.user.UserRepository
import org.springframework.boot.CommandLineRunner
import com.example.qa.user.LoadDataBase
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoadDataBase {
    @Bean
    fun initDataBase(repository: UserRepository?): CommandLineRunner {
        return CommandLineRunner { args: Array<String?>? -> }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoadDataBase::class.java)
    }
}