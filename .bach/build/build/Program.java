package build;

import com.github.sormuras.bach.Bach;
import com.github.sormuras.bach.BuildProgram;
import com.github.sormuras.bach.Builder;
import com.github.sormuras.bach.project.MainCodeSpace;
import com.github.sormuras.bach.project.Project;
import com.github.sormuras.bach.project.ProjectInfo;
import com.github.sormuras.bach.tool.Command;
import java.nio.file.Files;
import java.nio.file.Path;

public class Program implements BuildProgram {

  public Program() {}

  @Override
  public void build(Bach bach, String... args) {
    var project = Project.of(getClass().getModule().getAnnotation(ProjectInfo.class));
    new Custom(bach, project).build();
  }

  private static class Custom extends Builder {

    private Custom(Bach bach, Project project) {
      super(bach, project);
    }

    @Override
    public void buildMainCodeSpace(MainCodeSpace main)  {
      super.buildMainCodeSpace(main);

      // 0. "hello.war"
      // 1. main classes -> WEB-INF/classes
      // 2. external JARs -> WEB-INF/lib
      // 3. static files -> WAR

      try {
        System.out.println("Make Love, Not War!");

        // 1
        var classes = Path.of(".bach/workspace/classes-main/11/com.mycompany.hello");
        var war = Path.of(".bach/workspace/war");
        var web = war.resolve("WEB-INF/classes");
        Files.createDirectories(web.getParent());
        Files.move(classes, web);

        // 2
        var lib = war.resolve("WEB-INF/lib");
        Files.createDirectories(lib);
        Files.move(Path.of(".bach/external-modules/io.github.classgraph.jar"), lib.resolve("classgraph.jar"));

        // 3
        Files.copy(Path.of("src/main/webapp/index.jsp"), war.resolve("index.jsp"));
        Files.copy(Path.of("src/main/webapp/WEB-INF/web.xml"), war.resolve("WEB-INF/web.xml"));

        // 0
        var jar =
            Command.builder("jar")
                .with("--create")
                .with("--file", "hello.war")
                .with("-C", war, ".")
                .build();

        run(jar);
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }
    }
  }
}
