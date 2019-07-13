package pl.wojciechkabat.hotchilli

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class HotchilliApplication

fun main(args: Array<String>) {
    runApplication<HotchilliApplication>(*args)
}
