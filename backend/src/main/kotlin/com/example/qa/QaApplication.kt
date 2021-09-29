package com.example.qa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QaApplication

fun main(args: Array<String>) {
	runApplication<QaApplication>(*args)
}
