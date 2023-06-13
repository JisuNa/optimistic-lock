package com.example.optimisticlock.controller

import com.example.optimisticlock.dto.BookRequestDto
import com.example.optimisticlock.service.BookService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun book(@RequestBody bookRequestDto: BookRequestDto) {
        bookService.book(bookRequestDto)
    }
}
