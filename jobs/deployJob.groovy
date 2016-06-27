job('deployJob') {
    wrappers {
        preBuildCleanup()
    }
    steps {
        shell('echo kek')
    }
}
