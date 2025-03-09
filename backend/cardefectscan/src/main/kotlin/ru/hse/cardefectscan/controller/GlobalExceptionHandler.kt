package ru.hse.cardefectscan.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.hse.cardefectscan.exception.BusinessException
import ru.hse.cardefectscan.exception.LoginOrPasswordIncorrectException
import ru.hse.cardefectscan.exception.SessionNotFoundException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(LoginOrPasswordIncorrectException::class)
    fun handleLoginOrPasswordIncorrect(ex: LoginOrPasswordIncorrectException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED).apply {
            title = HttpStatus.UNAUTHORIZED.reasonPhrase
            detail = ex.message ?: "Login or password is incorrect"
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail)
    }

    @ExceptionHandler(SessionNotFoundException::class)
    fun handleNoSessionException(ex: SessionNotFoundException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED).apply {
            title = HttpStatus.UNAUTHORIZED.reasonPhrase
            detail = ex.message ?: "User session not found"
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail)
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            title = HttpStatus.BAD_REQUEST.reasonPhrase
            detail = ex.message ?: "Business error occurred"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }
}
