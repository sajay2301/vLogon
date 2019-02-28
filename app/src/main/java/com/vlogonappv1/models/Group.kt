package com.vlogonappv1.models


import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "groups", indices = [(Index(value = ["id"], unique = true))])
data class Group(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "contacts_count") var contactsCount: Int = 0) : Serializable {

    fun addContact() = contactsCount++

    fun getBubbleText() = title

    fun isPrivateSecretGroup() = id ?: 0 >= FIRST_GROUP_ID
}
