package com.example.optimisticlock.service

import com.example.optimisticlock.dto.BookRequestDto
import com.example.optimisticlock.repository.SeatRepository
import io.kotest.core.spec.style.BehaviorSpec
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest(
    private val bookService: BookService,
    private val seatRepository: SeatRepository
) : BehaviorSpec({

    given("100명이 동시에 요청한 상황에서") {

        var count = 0
        val numberOfThreads = 100
        val latch = CountDownLatch(numberOfThreads)
        val executor = Executors.newFixedThreadPool(1)

        `when`("횟수 저장 요청하면") {
            for (i in 0 until numberOfThreads) {
                executor.submit {

                    val mockBookRequestDto = BookRequestDto(userId = i.toLong(), seatNumber = "A-01")

                    bookService.book(mockBookRequestDto)
                    latch.countDown()
                }
            }
            latch.await()

//            val result = withContext(Dispatchers.IO) {
//                seatRepository.findBySeatNumber()
//            }
//
//            then("100 횟수 저장된다.") {
//                result?.count shouldBe 100
//            }
        }
    }
})