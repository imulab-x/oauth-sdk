package io.imulab.astrea.sdk.oauth

import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.then
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object ExtensionsSpec : Spek({

    given("A string-to-list map with duplicate entries") {
        val map: Map<String, List<String>> = mapOf("foo" to listOf("a", "b"), "bar" to listOf("c"))

        `when`("accessing multi-valued entry foo") {
            val result = runCatching { map.singleOrNull("foo") }

            then("should raise error") {
                assertThat(result.isFailure).isTrue()
            }
        }

        `when`("accessing single-valued entry bar") {
            val result = runCatching { map.singleOrNull("bar") }

            then("should return value") {
                assertThat(result.isSuccess).isTrue()
                assertThat(result.getOrNull()).isEqualTo("c")
            }
        }

        `when`("accessing non-existing entry zoo") {
            val result = runCatching { map.singleOrNull("zoo") }

            then("should return null") {
                assertThat(result.isSuccess).isTrue()
                assertThat(result.getOrNull()).isNull()
            }
        }
    }

    given("A mutable string-string map") {
        val map = mutableMapOf<String, String>()

        `when`("put a non-empty value") {
            map.putIfNotEmpty("a", "b")

            then("new entry exists in map") {
                assertThat(map["a"]).isEqualTo("b")
            }
        }

        `when`("put an empty value") {
            map.putIfNotEmpty("c", "")

            then("new entry does not exist in map") {
                assertThat(map.containsKey("c")).isFalse()
            }
        }
    }

    given("Scope format checking extension") {
        `when`("checking a well-formed scope") {
            val result = runCatching { "foo".mustNotMalformedScope() }

            then("should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("checking a malformed scope") {
            val result = runCatching { "\"foo".mustNotMalformedScope() }

            then("should fail") {
                assertThat(result.isFailure).isTrue()
            }
        }
    }

    given("An exactly set extension") {
        `when`("checking a set with exactly one item") {
            val result = setOf("foo").exactly("foo")

            then("should return true") {
                assertThat(result).isTrue()
            }
        }

        `when`("checking a set with exactly another item") {
            val result = setOf("foo").exactly("bar")

            then("should return false") {
                assertThat(result).isFalse()
            }
        }

        `when`("checking a set with multiple items") {
            val result = setOf("foo", "bar").exactly("foo")

            then("should return false") {
                assertThat(result).isFalse()
            }
        }

        `when`("checking an empty set") {
            val result = emptySet<String>().exactly("foo")

            then("should return false") {
                assertThat(result).isFalse()
            }
        }
    }
})