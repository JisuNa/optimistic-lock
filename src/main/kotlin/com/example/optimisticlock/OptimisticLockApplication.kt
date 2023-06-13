package com.example.optimisticlock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OptimisticLockApplication

fun main(args: Array<String>) {
    runApplication<OptimisticLockApplication>(*args)
}
