job('dockerBuildJob') {
    steps {
        resolveArtifacts {
            failOnError()
        }
        shell('echo kek')
    }
}
