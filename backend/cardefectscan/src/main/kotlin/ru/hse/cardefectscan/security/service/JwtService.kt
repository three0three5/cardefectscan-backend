package ru.hse.cardefectscan.security.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Service
import ru.hse.cardefectscan.security.model.JwtClaims
import ru.hse.cardefectscan.security.model.Role
import ru.hse.cardefectscan.security.properties.AuthProperties
import java.time.Instant


@Service
class JwtService(
    private val authProperties: AuthProperties,
) {
    fun createJwt(userid: Int, roles: List<String?>?): String {
        val algorithm: Algorithm = Algorithm.HMAC512(authProperties.privateKey)
        return JWT.create()
            .withIssuer(ISSUER)
            .withIssuedAt(Instant.now())
            .withClaim(USER_ID_CLAIM, userid)
            .withClaim(ROLES_CLAIM, roles)
            .withExpiresAt(Instant.now().plusSeconds(authProperties.jwtLifespan))
            .sign(algorithm)
    }

    fun verify(token: String?): DecodedJWT {
        val algorithm: Algorithm = Algorithm.HMAC512(authProperties.privateKey)
        val verifier: JWTVerifier = JWT
            .require(algorithm)
            .withIssuer(ISSUER)
            .build()
        return verifier.verify(token)
    }

    fun getClaims(jwt: DecodedJWT): JwtClaims {
        val userId = jwt.getClaim(USER_ID_CLAIM).asLong()
        val roles: List<Role> = jwt.getClaim(ROLES_CLAIM).asList(Role::class.java)
        return JwtClaims(
            userId = userId,
            roles = roles,
        )
    }

    companion object {
        const val ISSUER = "cardefectscan"
        const val USER_ID_CLAIM = "userId"
        const val ROLES_CLAIM = "roles"
    }
}