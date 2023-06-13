package com.example.optimisticlock.service

import com.example.optimisticlock.dto.BookRequestDto
import com.example.optimisticlock.repository.SeatRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(private val seatRepository: SeatRepository) {
    @Transactional
    fun book(bookRequestDto: BookRequestDto) {
        seatRepository.findBySeatNumber(bookRequestDto.seatNumber) ?: throw RuntimeException()
        val a = seatRepository.updateUserIdBySeatNumber(bookRequestDto.userId, bookRequestDto.seatNumber)
        println(a)
    }
}
