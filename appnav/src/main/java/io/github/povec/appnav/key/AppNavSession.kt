package io.github.povec.appnav.key

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * [AppNavSession]
 * 履歴の独立性を保証する単位。
 */
@Serializable
sealed interface AppNavSession {
    val name: String
    val identifier: String

    @Serializable
    data class Specific(override val name: String) : AppNavSession {
        override val identifier: String get() = Type.SPECIFIC - name
    }

    @Serializable
    data class Managed(override val name: String) : AppNavSession {
        override val identifier: String get() = Type.MANAGED - name
    }

    @Serializable
    data class General(override val name: String = UUID.randomUUID().toString()) : AppNavSession {
        override val identifier: String get() = Type.GENERAL - name
    }

    @JvmInline
    value class Type private constructor(val value: String) {
        override fun toString(): String = value

        operator fun minus(name: String) = toString() + SEPARATOR + name

        companion object {
            const val SEPARATOR = ":"

            val SPECIFIC = Type("specific")
            val MANAGED = Type("managed")
            val GENERAL = Type("general")

        }

    }
}