package org.xtimms.shirizu.core.parser.local.input

import androidx.core.net.toFile
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaPage
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.toCamelCase
import org.xtimms.shirizu.core.model.LocalManga
import org.xtimms.shirizu.core.model.LocalMangaSource
import org.xtimms.shirizu.core.parser.local.MangaIndex
import org.xtimms.shirizu.core.parser.local.hasCbzExtension
import org.xtimms.shirizu.core.parser.local.output.LocalMangaOutput
import org.xtimms.shirizu.utils.AlphanumComparator
import org.xtimms.shirizu.utils.hasImageExtension
import org.xtimms.shirizu.utils.lang.longHashCode
import org.xtimms.shirizu.utils.lang.toListSorted
import org.xtimms.shirizu.utils.system.children
import org.xtimms.shirizu.utils.system.creationTime
import org.xtimms.shirizu.utils.system.walkCompat
import java.io.File
import java.util.TreeMap
import java.util.zip.ZipFile

/**
 * Manga {Folder}
 * |--- index.json (optional)
 * |--- Chapter 1.cbz
 *   |--- Page 1.png
 *   :
 *   L--- Page x.png
 * |--- Chapter 2.cbz
 * :
 * L--- Chapter x.cbz
 */
class LocalMangaDirInput(root: File) : LocalMangaInput(root) {

    override suspend fun getManga(): LocalManga = runInterruptible(Dispatchers.IO) {
        val index = MangaIndex.read(File(root, LocalMangaOutput.ENTRY_NAME_INDEX))
        val mangaUri = root.toUri().toString()
        val chapterFiles = getChaptersFiles()
        val info = index?.getMangaInfo()
        val cover = fileUri(
            root,
            index?.getCoverEntry() ?: findFirstImageEntry().orEmpty(),
        )
        val manga = info?.copy2(
            source = LocalMangaSource,
            url = mangaUri,
            coverUrl = cover,
            largeCoverUrl = cover,
            chapters = info.chapters?.mapIndexedNotNull { i, c ->
                val fileName = index.getChapterFileName(c.id)
                val file = if (fileName != null) {
                    chapterFiles[fileName]
                } else {
                    // old downloads
                    chapterFiles.values.elementAtOrNull(i)
                } ?: return@mapIndexedNotNull null
                c.copy(url = file.toUri().toString(), source = LocalMangaSource)
            },
        ) ?: Manga(
            id = root.absolutePath.longHashCode(),
            title = root.name.toHumanReadable(),
            url = mangaUri,
            publicUrl = mangaUri,
            source = LocalMangaSource,
            coverUrl = findFirstImageEntry().orEmpty(),
            chapters = chapterFiles.values.mapIndexed { i, f ->
                MangaChapter(
                    id = "$i${f.name}".longHashCode(),
                    name = f.nameWithoutExtension.toHumanReadable(),
                    number = 0f,
                    volume = 0,
                    source = LocalMangaSource,
                    uploadDate = f.creationTime,
                    url = f.toUri().toString(),
                    scanlator = null,
                    branch = null,
                )
            },
            altTitle = null,
            rating = -1f,
            isNsfw = false,
            tags = setOf(),
            state = null,
            author = null,
            largeCoverUrl = null,
            description = null,
        )
        LocalManga(manga, root)
    }

    override suspend fun getMangaInfo(): Manga? = runInterruptible(Dispatchers.IO) {
        val index = MangaIndex.read(File(root, LocalMangaOutput.ENTRY_NAME_INDEX))
        index?.getMangaInfo()
    }

    override suspend fun getPages(chapter: MangaChapter): List<MangaPage> = runInterruptible(Dispatchers.IO) {
        val file = chapter.url.toUri().toFile()
        if (file.isDirectory) {
            file.children()
                .filter { it.isFile && hasImageExtension(it) }
                .toListSorted(compareBy(AlphanumComparator()) { x -> x.name })
                .map {
                    val pageUri = it.toUri().toString()
                    MangaPage(pageUri.longHashCode(), pageUri, null, LocalMangaSource)
                }
        } else {
            ZipFile(file).use { zip ->
                zip.entries()
                    .asSequence()
                    .filter { x -> !x.isDirectory }
                    .map { it.name }
                    .toListSorted(AlphanumComparator())
                    .map {
                        val pageUri = zipUri(file, it)
                        MangaPage(
                            id = pageUri.longHashCode(),
                            url = pageUri,
                            preview = null,
                            source = LocalMangaSource,
                        )
                    }
            }
        }
    }

    private fun String.toHumanReadable() = replace("_", " ").toCamelCase()

    private fun getChaptersFiles() = root.walkCompat(includeDirectories = true)
        .filter { it != root && it.isChapterDirectory() || it.hasCbzExtension() }
        .associateByTo(TreeMap(AlphanumComparator())) { it.name }

    private fun findFirstImageEntry(): String? {
        return root.walkCompat(includeDirectories = false)
            .firstOrNull { hasImageExtension(it) }?.toUri()?.toString()
            ?: run {
                val cbz = root.walkCompat(includeDirectories = false)
                    .firstOrNull { it.hasCbzExtension() } ?: return null
                ZipFile(cbz).use { zip ->
                    zip.entries().asSequence()
                        .firstOrNull { !it.isDirectory && hasImageExtension(it.name) }
                        ?.let { zipUri(cbz, it.name) }
                }
            }
    }

    private fun fileUri(base: File, name: String): String {
        return File(base, name).toUri().toString()
    }

    private fun File.isChapterDirectory(): Boolean {
        return isDirectory && children().any { hasImageExtension(it) }
    }
}
