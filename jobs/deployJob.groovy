job('deployJob') {
    label('staging')
    wrappers {
        preBuildCleanup()
    }
    parameters {
        stringParam('GDLIB_VERSION')
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
          export GDLIB_VERSION=${GL_APP_VERSION}-${GL_TIME_STAMP}-${GL_BUILD_ID}
          export GDLIB_ENV=prod
          export NEXUS_REPOSITORY=builds-all
          bash provide_artifacts.sh
          docker-compose build

          export GDLIB_REGISTRY_REPO=172.26.30.4:5000/gridlibrary/
          docker login -u gridlibrary -p gridlibrary 172.26.30.4:5000
          images=(app db images)
          for image in ${images[@]}; do
            docker tag gdlib-${image} ${GDLIB_REGISTRY}gdlib-${image}:2.0.0.${BUILD_ID}
            docker push ${GDLIB_REGISTRY}gdlib-${image}:2.0.0.${BUILD_ID}
          done;
          DOCKER_HOST=prod1.gridlibrary.c4gd-orion.griddynamics.net:2376 docker-compose up -d
          sleep 30
          DOCKER_HOST=prod1.gridlibrary.c4gd-orion.griddynamics.net:2376 docker-compose logs
        '''.stripIndent())       
    }
    publishers {
      buildPipelineTrigger('deployJob')
    }
}
