package org.jugru.plugins

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.serialization.Serializable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

@Serializable
data class SpeakerEntity(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val description: String,
)

class SpeakerService(private val database: Database) {
    object Speakers : IntIdTable("speakers") {
        val firstName = varchar("first_name", 50)
        val lastName = varchar("last_name", 50)
        val age = integer("age")
        val description = varchar("description", 255)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Speakers)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(user: SpeakerEntity): Int =
        dbQuery {
            Speakers.insert {
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[age] = user.age
                it[description] = user.description
            }[Speakers.id].value
        }

    suspend fun read(id: Int): SpeakerEntity? =
        dbQuery {
            Speakers.selectAll()
                .where { Speakers.id eq id }
                .map {
                    SpeakerEntity(
                        it[Speakers.id].value,
                        it[Speakers.firstName],
                        it[Speakers.lastName],
                        it[Speakers.age],
                        it[Speakers.description],
                    )
                }.singleOrNull()
        }

    suspend fun readAll(): List<SpeakerEntity> {
        return dbQuery {
            Speakers.selectAll()
                .map {
                    SpeakerEntity(
                        it[Speakers.id].value,
                        it[Speakers.firstName],
                        it[Speakers.lastName],
                        it[Speakers.age],
                        it[Speakers.description],
                    )
                }
        }
    }

    suspend fun update(
        id: Int,
        user: SpeakerEntity,
    ): Unit =
        dbQuery {
            Speakers.update({ Speakers.id eq id }) {
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[age] = user.age
                it[description] = user.description
            }
        }

    suspend fun delete(id: Int): Unit =
        dbQuery {
            Speakers.deleteWhere { Speakers.id eq id }
        }
}

