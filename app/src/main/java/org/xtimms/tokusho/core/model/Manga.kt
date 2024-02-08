package org.xtimms.tokusho.core.model

import org.koitharu.kotatsu.parsers.model.Manga

fun Collection<Manga>.distinctById() = distinctBy { it.id }