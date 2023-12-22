import java.util.Iterator;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

class PatchBankBuilder {
  static final String cwd = System.getProperty("user.dir");
  static final Path target = Path.of(cwd, "PatchBank");
  static final Path srcDir = Path.of(cwd, "patches");
  static final Path blankPch = srcDir.resolve("NULL.pch");
  static final Path pchListFile = target.resolve("Bank1.pchList");

  static int printFillerLine(PrintWriter out) {
    out.printf("%s\r\n", blankPch.getFileName());
    return 1;
  }

  public static void main(String[] args) throws IOException {
    /* Read the patch directories */
    TreeSet<Section> sections = new TreeSet<Section>();
    for(Path patchDir : Files.newDirectoryStream(srcDir))
      if(Files.isDirectory(patchDir))
        sections.add(new Section(patchDir));

    /* Check that no group overflows into the next, and remove empty groups */
    int end = 99;
    Iterator<Section> desc = sections.descendingIterator();
    while(desc.hasNext()) {
      Section s = desc.next();
      if(s.size() == 0) desc.remove();
      else end = s.checkSize(end);
    }

    /* Start with an empty target directory; remove any existing files */
    System.out.println("Destination: " + target);
    if(Files.isDirectory(target))
      for(Path destFile : Files.newDirectoryStream(target))
        Files.delete(destFile);
    else
      Files.createDirectory(target);

    /* Create symlinks to source files in bank directory */
    for(Section s : sections)
      s.createSymlinks();

    /* Create the patch list file */
    System.out.println();
    int line = 1, nPatches = 0;
    PrintWriter out = new PrintWriter(Files.newBufferedWriter(pchListFile));
    out.printf("1 \r\n");
    for(Section s : sections) {
      line = s.printPatchFiles(out, line);
      nPatches += s.size();
    }
    out.close();

    /* Print the summary */
    System.out.println();
    System.out.printf("%d patch%s in %d section%s\n\n", nPatches,
                      (nPatches == 1 ? "" : "es"), sections.size(),
                      (sections.size() == 1 ? "" : "s"));
  }

  static class Section implements Comparable<Section> {
    private String dir;
    private int start, end;
    private TreeSet<Path> patchFiles;

    public Section(Path path) throws DirectoryIteratorException, IOException {
      dir = path.getFileName().toString();
      start = Integer.parseInt(dir.substring(0, 2));
      patchFiles = new TreeSet<Path>();
      DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.pch");
      for(Path entry : stream) patchFiles.add(entry);
      end = start + size() - 1;
    }

    public int size() { return patchFiles.size(); }

    public int checkSize(int bound) {
      if(bound < end)
        throw new RuntimeException("Group " + dir + " is too large");
      return start - 1;
    }

    public void createSymlinks() throws IOException {
      for(Path p : patchFiles)
        Files.createSymbolicLink(target.resolve(p.getFileName()),
                                 target.relativize(p));
    }

    public int printPatchFiles(PrintWriter out, int line) {
      while(line < start)
        line += printFillerLine(out);
      for(Path p : patchFiles) {
        String name = p.getFileName().toString();
        int len = name.length() - 4;
        out.printf("%s\r\n", name);
        System.out.printf("%02d \t %s\n", line++, name.substring(0, len));
      }
      return line;
    }

    public int compareTo(Section other) {
      return Integer.compare(start, other.start);
    }
  }
}
