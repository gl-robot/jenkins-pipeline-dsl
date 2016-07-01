try {
  def workspace = currentBuild.getEnvironment()["WORKSPACE"]
  def timestamp = new Date().format("yyyyMMdd.hhmmss")
  def version = new XmlSlurper()
                    .parse(workspace + File.separator + "pom.xml")
                    .version.toString()
                    .tokenize('-SNAPSHOT')[0]
           
  return [
    APP_VERSION: version,
    TIME_STAMP: timestamp
  ]
} catch (Exception e) {
  println e
}

