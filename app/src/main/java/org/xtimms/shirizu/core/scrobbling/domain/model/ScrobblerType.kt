package org.xtimms.shirizu.core.scrobbling.domain.model

import javax.inject.Qualifier

@Qualifier
annotation class ScrobblerType(
    val service: ScrobblerService
)