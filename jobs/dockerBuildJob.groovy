
job('dockerBuildJob') {
    parameters {
        stringParam('GL_APP_VERSION')
        stringParam('GL_TIME_STAMP')
        stringParam('GL_BUILD_ID')
    }
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
          export GDLIB_VERSION=${GL_APP_VERSION}-${GL_TIME_STAMP}-${GL_BUILD_ID}
          export NEXUS_REPOSITORY=test
          export DOCKER_REGISTRY=172.26.6.4:5000
          cd grid-library-containers
          bash provide_artifacts.sh
          docker-compose build 
        '''.stripIndent())       
    }
    publishers {
      buildPipelineTrigger('deployJob')
    }
}
