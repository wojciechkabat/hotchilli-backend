package pl.wojciechkabat.hotchilli

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HotchilliApplication

fun main(args: Array<String>) {
    runApplication<HotchilliApplication>(*args)
}
