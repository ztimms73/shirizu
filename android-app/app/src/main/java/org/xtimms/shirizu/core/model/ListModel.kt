package org.xtimms.shirizu.core.model

interface ListModel {

    override fun equals(other: Any?): Boolean

    fun areItemsTheSame(other: ListModel): Boolean

    fun getChangePayload(previousState: ListModel): Any? = null
}