package com.example.teashowcase.data

import android.os.SystemClock
import com.example.teashowcase.domain.entities.AnswerResult
import com.example.teashowcase.domain.entities.Game
import com.example.teashowcase.domain.repository.GameRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

class GameRepositoryImpl @Inject constructor() : GameRepository {

    override fun loadGame(): Single<Game> {
        return produceErrorWithProbability(
            errorProbability = 15,
            maxErrorDelayMillis = 5000
        ) {
            Single.fromCallable {
                Game(
                    id = Random.nextInt(0, 101).toString() + "-" + SystemClock.elapsedRealtime()
                )
            }
                .delaySubscription(Random.nextLong(0, 5000), TimeUnit.MILLISECONDS, Schedulers.io())
        }
    }

    override fun checkAnswer(gameId: String, answer: Int): Single<AnswerResult> {
        return produceErrorWithProbability() {
            Single.fromCallable {
                val correctAnswer = gameId.split("-")[0].toInt()
                when {
                    answer < correctAnswer -> AnswerResult.LESS
                    answer > correctAnswer -> AnswerResult.MORE
                    else -> AnswerResult.EQUAL
                }
            }
                .delaySubscription(Random.nextLong(0, 3000), TimeUnit.MILLISECONDS, Schedulers.io())
        }
    }

    private fun <T> produceErrorWithProbability(
        errorProbability: Int = 10,
        maxErrorDelayMillis: Long = 3000,
        regularResultProvider: () -> Single<T>
    ): Single<T> {
        return if ((Random.nextInt(0, 100) > errorProbability)) {
            regularResultProvider()
        } else {
            Single.error<T>(ArithmeticException())
                .delaySubscription(
                    Random.nextLong(0, maxErrorDelayMillis),
                    TimeUnit.MILLISECONDS,
                    Schedulers.io()
                )
        }
    }
}
