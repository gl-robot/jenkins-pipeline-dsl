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
            branch('dev')
        }
    } 
    steps {
        shell('''
          docker exec -i gridlibrarycontainers_db_1 psql -U postgres -c 'DROP SCHEMA public CASCADE; CREATE SCHEMA public';
          export GDLIB_VERSION=${GL_APP_VERSION}-${GL_TIME_STAMP}-${GL_BUILD_ID}
          export GDLIB_ENV=ci
          export NEXUS_STORAGE=http://172.26.30.4:8081/service/local/artifact/maven/redirect
          export NEXUS_REPOSITORY=builds-all
          export DOCKER_REGISTRY=172.26.6.4:5000
          export GDLIB_REGISTRY_REPO=''
          cd grid-library-containers
          bash provide_artifacts.sh
          docker-compose build 
          docker-compose stop
          docker-compose up -d
          
          if [[ $(timeout 30 docker wait gridlibrarycontainers_app_1 -ne 0 ]]; then 
              docker logs gridlibrarycontainers_app_1
          fi
          '''.stripIndent())       
    }
}
