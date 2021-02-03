name := "TFPetProject"

version := "0.1"


scalaVersion := "2.12.8"


libraryDependencies +=
  "org.typelevel" %% "cats-tagless-macros" % "0.12" //latest version indicated in the badge above




Compile / scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n >= 13 => "-Ymacro-annotations" :: Nil
    case _ => Nil
  }
}

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n >= 13 => Nil
    case _ => compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full) :: Nil
  }
}

//addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")