package com.example.optimisticlock.service

import com.example.optimisticlock.dto.BookRequestDto
import com.example.optimisticlock.repository.SeatRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class BookServiceTest(private val bookService: BookService) : BehaviorSpec({

    given("100명이 동시에 요청한 상황에서") {

        var updateCount = 0
        val numberOfThreads = 100
        val latch = CountDownLatch(numberOfThreads)
        val executor = Executors.newFixedThreadPool(1)

        `when`("횟수 저장 요청하면") {
            for (i in 0 until numberOfThreads) {
                executor.submit {

                    val mockBookRequestDto = BookRequestDto(userId = i.toLong(), seatNumber = "A-01")

                    updateCount += bookService.book(mockBookRequestDto)
                    latch.countDown()
                }
            }

            withContext(Dispatchers.IO) {
                latch.await()
            }

            then("업데이트가 1건만 발생한다.") {
                updateCount shouldBe 1
            }
        }
    }
})