package org.xtimms.etsudoku.utils

import org.xtimms.etsudoku.utils.iterator.CloseableIterator
import org.xtimms.etsudoku.utils.iterator.MappingIterator
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class FileSequence(private val dir: File) : Sequence<File> {

    override fun iterator(): Iterator<File> {
        val stream = Files.newDirectoryStream(dir.toPath())
        return CloseableIterator(MappingIterator(stream.iterator(), Path::toFile), stream)
    }
}