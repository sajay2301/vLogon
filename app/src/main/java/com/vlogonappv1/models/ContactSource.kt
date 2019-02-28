package com.vlogonappv1.models



data class ContactSource(var name: String, var type: String, var publicName: String) {
    fun getFullIdentifier(): String {
        return if (type == SMT_PRIVATE) {
            type
        } else {
            "$name:$type"
        }
    }
}
