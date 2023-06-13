package com.example.optimisticlock.repository

import com.example.optimisticlock.entity.Seat
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface SeatRepository: JpaRepository<Seat, Long> {
    fun findBySeatNumber(seatNumber: String): Seat?

    @Modifying
    @Query("""
        update Seat
        set userId = :userId
        where seatNumber = :seatNumber
        and userId is null
    """)
    fun updateUserIdBySeatNumber(userId: Long, seatNumber: String): Int
}