import com.github.sormuras.bach.project.ProjectInfo;

@ProjectInfo(version = "1", compileModulesForJavaRelease = 11)
module build {
  requires com.github.sormuras.bach;

  provides com.github.sormuras.bach.BuildProgram with build.Program;
}
