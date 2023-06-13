# Optimistic Lock

## Overview

최근까지 데이터베이스 락을 걸 때 비관적 락을 사용하고 낙관적 락은 사용할 경우가 없다고 생각했다.<br/>
JPA에서 낙관적락은 `@Version` 을 사용하여 version 컬럼 또는 updatedAt 컬럼을 활용한 방법으로 동시성을 처리한다.

하지만 낙관적락은 데이터베이스에 있는 개념이며 version 을 사용하지 않고도 다른 전략으로 동시성 처리를 할 수 있다.

그 동안 JPA의 낙관적 락이 version 으로 동작하는 것만 알았지만 낙관적락에 대해 정확히 알지 못했던 것 같다.

## Situation

오버부킹이 불가능한 기차 예매 시스템을 설계한다.<br/>
좌석을 모두 테이블에 넣어두고 예약 요청이 들어오면 Update 하는 형태로 한다.<br/>
로그인, 기차, 시간, 에러제어 등은 고려하지 않고 좌석 예매에만 초점을 맞춘다.

## Stack

- Kotlin
- Spring boot 3.1
- Spring data JPA
- MySQL8
- Kotest

## ERD

<img src="./docs/erd.png" alt="drawing" width="400">

## Solution Strategy

```kotlin
@Transactional
fun book(bookRequestDto: BookRequestDto): Int {
    seatRepository.findBySeatNumber(bookRequestDto.seatNumber) ?: throw RuntimeException()

    return seatRepository.updateUserIdBySeatNumber(
        userId = bookRequestDto.userId,
        seatNumber = bookRequestDto.seatNumber
    )
}
```

해당 코드는 서비스에서 예약을 처리하는 메소드이다. 요청한 좌석이 있는지 확인하고 좌석을 예약을 한다.

### 낙관적 락

좌석 예약 쿼리를 살펴보자.

```kotlin
@Modifying
@Query("""
    update Seat
    set userId = :userId
    where seatNumber = :seatNumber
    and userId is null
""")
fun updateUserIdBySeatNumber(userId: Long, seatNumber: String): Int
```

조건절을 보면 `userId is null` 을 확인할 수 있다.

`A-01` 이라는 좌석이 아직 예매가 되지 않았다면, userId는 null 상태이다. <br/>
동시에 100,000명의 사용자가 같은 좌석 예약을 시도하면 하나의 사용자만이 업데이트에 성공으로 예약이 되고 나머지는 사용자들은 업데이트가 되지 않고 예약에 실패하게 된다.

### 업데이트 결과

그렇다면 업데이트가 되지 않은 트랜잭션은 에러가 발생할까?

그렇지 않다. 업데이트가 발생한 건수를 리턴한다.

```sql
select * from seat;

+----+---------+-------------+
| id | user_id | seat_number |
+----+---------+-------------+
|  1 |   <null>| A-01        |
+----+---------+-------------+
```

아직 예약이 되지 않은 상태에서 예약을 하면 1 결과를 얻을 수 있다.

```sql
select * from seat;

+----+---------+-------------+
| id | user_id | seat_number |
+----+---------+-------------+
|  1 |       1 | A-01        |
+----+---------+-------------+
```

예약이 된 상태에서 예약을 시도 하면 업데이트 발생한 건수는 0건이므로 결과로 0을 얻을 수 있다.

## Conclusion

JPA 낙관적락도 결국에 데이터베이스 낙관적락을 구현한 것에 불과하다는 것과 요구사항에 따라 낙관적 락을 사용한 전략도 필요하다는 것을 깨달았다.

추가적으로 해당 전략은 동시에 모든 사용자가 DB에 업데이트 요청을 하기 때문에 DB Connection Pool 크기도 고민할 필요가 있다.

DB Connection Pool 내용은 따로 다루도록 하자.
