package com.example.optimisticlock.service

import com.example.optimisticlock.dto.BookRequestDto
import com.example.optimisticlock.repository.SeatRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(private val seatRepository: SeatRepository) {
    @Transactional
    fun book(bookRequestDto: BookRequestDto): Int {
        seatRepository.findBySeatNumber(bookRequestDto.seatNumber) ?: throw RuntimeException()

        return seatRepository.updateUserIdBySeatNumber(
            userId = bookRequestDto.userId,
            seatNumber = bookRequestDto.seatNumber
        )
    }
}
