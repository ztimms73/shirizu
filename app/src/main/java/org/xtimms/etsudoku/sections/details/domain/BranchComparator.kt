package org.xtimms.etsudoku.sections.details.domain

import org.xtimms.etsudoku.sections.details.model.MangaBranch

class BranchComparator : Comparator<MangaBranch> {

    override fun compare(o1: MangaBranch, o2: MangaBranch): Int = compareValues(o1.name, o2.name)
}