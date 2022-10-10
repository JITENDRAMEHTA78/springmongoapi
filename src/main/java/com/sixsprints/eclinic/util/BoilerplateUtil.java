package com.sixsprints.eclinic.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

/**
 * Just change <code>PACKAGE_PREFIX</code>, <code>fileName</code> and
 * <code>baseDir</code> and this classes (domain, repo, service and service
 * implementation) will create required java class for you.
 */
public class BoilerplateUtil {

  private static final String PACKAGE_PREFIX = "com.sixsprints.eclinic.";

  public static void main(String[] args) throws Exception {

    String fileName = "Appointment";
    if (StringUtils.isEmpty(fileName)) {
      throw new IllegalArgumentException();
    }
    createFiles(fileName);
    System.out.println("Done!");
  }

  private static void createFiles(String fileName) throws IOException {
    String baseDir = "/home/sudip/Downloads/sudip/workspace/eclinic-api/src/main/java/";
    String packageBase = PACKAGE_PREFIX.replace(".", "/");
    String packImportBase = "package " + PACKAGE_PREFIX + "";

    createDomain(fileName, baseDir, packageBase, packImportBase);
    createRepo(fileName, baseDir, packageBase, packImportBase);
    createService(fileName, baseDir, packageBase, packImportBase);
    createServiceImpl(fileName, baseDir, packageBase, packImportBase);

  }

  private static void createServiceImpl(String fileName, String baseDir, String packageBase, String packImportBase)
    throws IOException {
    String path = baseDir + packageBase + "/service/impl/" + fileName + "ServiceImpl.java";
    String data = packImportBase + "service.impl;" + "\n" + "import " + PACKAGE_PREFIX + "domain." + fileName + ";"
      + "import org.springframework.stereotype.Service;" + "import " + PACKAGE_PREFIX + "service.AbstractCrudService;"
      + "import " + PACKAGE_PREFIX + "service." + fileName + "Service;" + "import " + PACKAGE_PREFIX + "repository."
      + fileName + "Repository;" + "import " + PACKAGE_PREFIX + "generic.GenericRepository;"
      + "@Service public class " + fileName + "ServiceImpl" + " extends AbstractCrudService<" + fileName
      + "> implements "
      + fileName + "Service {" + "@Resource private " + fileName + "Repository " + camelCase(fileName)
      + "Repository;" + "@Override protected GenericRepository<" + fileName + "> repository() { return "
      + camelCase(fileName) + "Repository;}" + "}";

    writeFile(path, data);
  }

  private static String camelCase(String fileName) {
    if (fileName.toUpperCase().equals(fileName)) {
      return fileName.toLowerCase();
    }
    return fileName.substring(0, 1).toLowerCase().concat(fileName.substring(1));
  }

  private static void createService(String fileName, String baseDir, String packageBase, String packImportBase)
    throws IOException {
    String path = baseDir + packageBase + "/service/" + fileName + "Service.java";
    String data = packImportBase + "service;" + "\n" + "import " + PACKAGE_PREFIX + "domain." + fileName + "; import "
      + PACKAGE_PREFIX + "service.GenericCrudService;" + "public interface " + fileName + "Service"
      + " extends GenericCrudService<" + fileName + ">" + " {}";

    writeFile(path, data);
  }

  private static void createRepo(String fileName, String baseDir, String packageBase, String packImportBase)
    throws IOException {
    String path = baseDir + packageBase + "/repository/" + fileName + "Repository.java";
    String data = packImportBase + "repository;" + "\n" + "import " + PACKAGE_PREFIX + "domain." + fileName
      + "; import " + PACKAGE_PREFIX + "generic.GenericRepository;" + "\n @Repository @JaversSpringDataAuditable \n"
      + "public interface " + fileName + "Repository" + " extends GenericRepository<" + fileName + ">" + " {}";

    writeFile(path, data);
  }

  private static void createDomain(String fileName, String baseDir, String packageBase, String packImportBase)
    throws IOException {
    String domainPath = baseDir + packageBase + "/domain/" + fileName + ".java";
    String domainData = packImportBase + "domain;" + "\n"
      + "import lombok.experimental.SuperBuilder; import lombok.Data; import lombok.EqualsAndHashCode;import lombok.AllArgsConstructor;import lombok.NoArgsConstructor;"
      + "@Data @SuperBuilder @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode(callSuper = true) @Document "
      + "public class " + fileName + " extends AbstractMongoEntity" + " {private String name;}";

    writeFile(domainPath, domainData);
  }

  private static void writeFile(String path, String data) throws IOException {
    Files.write(Paths.get(path), data.getBytes());
  }

}