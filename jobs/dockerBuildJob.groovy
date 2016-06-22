
job('dockerBuildJob') {
    scm {
        git {
            remote {
                github('griddynamics/books-elibrary', 'ssh')
                credentials('github-cicd-key')
            }
            branch('cicd-replatforming')
        }
    } 
    steps {
        shell('''
          export GDLIB_VERSION=${GL_APP_VESRION}-${GL_TIME_STAMP}-${GL_BUILD_ID}
          export NEXUS_REPOSITORY=pre-releases
          export DOCKER_REGISTRY=172.26.6.4:5000
          cd grid-library-containers
          bash provide_artifacts.sh
          docker-compose up -d
        '''.stripIndent())
    }
}
