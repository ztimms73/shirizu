package org.xtimms.shirizu.sections.details.domain

import org.xtimms.shirizu.sections.details.model.MangaBranch

class BranchComparator : Comparator<MangaBranch> {

    override fun compare(o1: MangaBranch, o2: MangaBranch): Int = compareValues(o1.name, o2.name)
}