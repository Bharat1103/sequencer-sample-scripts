addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.3")
resolvers ++= Seq(
  "GitBucket Snapshots Repository" at "http://localhost:8080/maven/snapshots",
  "GitBucket Releases Repository" at "http://localhost:8080/maven/releases"
)

// If repository is private, you have to add authentication information
credentials += Credentials("GitBucket Maven Repository",
                           "localhost",
                           "root",
                           "root")
