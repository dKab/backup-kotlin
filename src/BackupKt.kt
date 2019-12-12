import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors.toList
import java.io.FileOutputStream
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry

private fun prompt(msg : String) : String {
    print("$msg => ")
    return readLine() ?: ""
}

fun main(args: Array<String>) {
    val start = prompt("Enter a start path") //example: C:\documents
    val glob = prompt("Enter a glob pattern") //example: glob:*.txt
    var destination =  prompt("Enter a backup directory path")
    destination = if (!destination.isBlank()) destination else "C:\\Users\\dkaba\\backups\\"
    // Object a matcher object from the supplied Glob pattern
    val matcher = FileSystems.getDefault().getPathMatcher(glob)
    val path = Paths.get(start)
    // Walk the file system
    val archive = "$path\\archive.zip"
    val f = File(archive)
    val out = ZipOutputStream(FileOutputStream(f))
    Files.walk(path)
        // Filter out anything that doesn't match the glob
        .filter { path: Path? -> path?.let { matcher.matches(it.fileName) } ?: false }
        //Collect to a list
        .collect(toList())
        .forEach{
            // Print to the console
            println("Found ${it.fileName}")
            val entry = ZipEntry(it.toString())
            out.putNextEntry(entry)
            val bytes = Files.readAllBytes(it)
            out.write(bytes, 0, bytes.size)
            out.closeEntry()
        }
    out.close()
    val destinationFile = File("$destination\\backup.zip")
    f.copyTo(destinationFile, true)
    f.deleteRecursively()
}

