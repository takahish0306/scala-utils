package scala.utils

import java.io.{File=>JavaFile}
import org.apache.commons.io.{FilenameUtils,FileUtils}

object File {
  /**
   * Read string from a file.
   * 
   * @param path String A file path, such as "./foo/bar.txt"
   */
  def read(path: String): String = {
    val javaFile = buildJavaFile(path)
    FileUtils.readFileToString(javaFile)
  }

  /**
   * Write string to a file.
   *
   * @param path String A file path, such as "./foo/bar.txt"
   * @param string String String to be output
   */
  def write(path: String, string: String): Unit = {
    val javaFile = buildJavaFile(path)
    FileUtils.writeStringToFile(javaFile, string)
  }

  /**
   * Builds JavaFile object
   *
   * @param path String A file path, such as "./foo/bar.txt"
   */
  def buildJavaFile(path: String): JavaFile = {
    val normalizedPath = FilenameUtils.normalize(path)
    new JavaFile(normalizedPath)
  }
}
