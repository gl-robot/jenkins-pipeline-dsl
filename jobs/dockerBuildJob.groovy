job('dockerBuildJob') {
    label('staging')
    wrappers {
        preBuildCleanup()
    }
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
            branch('dev-alternate')
        }
    } 
    steps {
        shell('''
          docker exec -i gridlibrarycontainers_db_1 psql -U postgres -c 'DROP SCHEMA public CASCADE; CREATE SCHEMA public';
          export GDLIB_VERSION=${GL_APP_VERSION}-${GL_TIME_STAMP}-${GL_BUILD_ID}
          export NEXUS_REPOSITORY=test
          export DOCKER_REGISTRY=172.26.6.4:5000
          cd grid-library-containers
          bash provide_artifacts.sh
          docker-compose build 
          docker-compose up -d
        '''.stripIndent())       
    }
    publishers {
      buildPipelineTrigger('deployJob')
    }
}
