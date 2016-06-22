
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
          export GDLIB_VERSION=${APP_VESRION}-${TIME_STAMP}-${BUILD_ID}
          export NEXUS_REPOSITORY=pre-releases
          export DOCKER_REGISTRY=172.26.6.4:5000
          bash grid-library-containers/provide_artifacts.sh
          docker-compose up -d
        '''.stripIndent())
    }
}
