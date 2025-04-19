package ru.hse.cardefectscan.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.hse.cardefectscan.exception.BusinessException
import ru.hse.cardefectscan.exception.ImageNotFoundException
import ru.hse.cardefectscan.exception.LoginOrPasswordIncorrectException
import ru.hse.cardefectscan.exception.SessionNotFoundException
import ru.hse.cardefectscan.exception.UnauthorizedException
import ru.hse.cardefectscan.exception.UserExistsException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(LoginOrPasswordIncorrectException::class)
    fun handleLoginOrPasswordIncorrect(ex: LoginOrPasswordIncorrectException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED).apply {
            title = HttpStatus.UNAUTHORIZED.reasonPhrase
            detail = ex.message ?: "Неправильный логин или пароль"
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

    @ExceptionHandler(UserExistsException::class)
    fun handleSQLException(ex: UserExistsException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            title = HttpStatus.BAD_REQUEST.reasonPhrase
            detail = ex.message ?: "Пользователь с таким логином уже существует"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(ex: UnauthorizedException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN).apply {
            title = HttpStatus.FORBIDDEN.reasonPhrase
            detail = ex.message ?: "Access Denied"
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail)
    }

    @ExceptionHandler(ImageNotFoundException::class)
    fun handleImageNotFoundException(ex: ImageNotFoundException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
            title = HttpStatus.NOT_FOUND.reasonPhrase
            detail = ex.message ?: "Изображение не найдено"
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)
    }
}
