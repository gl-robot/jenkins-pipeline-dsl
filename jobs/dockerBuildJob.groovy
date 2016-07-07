job('dockerBuildJob') {
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
          docker tag ${DOCKER_REGISTRY}/gridlibrary/gdlib-app:${GDLIB_VERSION} ${DOCKER_REGISTRY}/gridlibrary/gdlib-app:${GDLIB_VERSION}
          docker tag ${DOCKER_REGISTRY}/gridlibrary/gdlib-db:${GDLIB_VERSION} ${DOCKER_REGISTRY}/gridlibrary/gdlib-db:${GDLIB_VERSION}
          docker tag ${DOCKER_REGISTRY}/gridlibrary/gdlib-images:${GDLIB_VERSION} ${DOCKER_REGISTRY}/gridlibrary/gdlib-images:${GDLIB_VERSION}
          docker push ${DOCKER_REGISTRY}/gridlibrary/gdlib-app:${GDLIB_VERSION}
          docker push ${DOCKER_REGISTRY}/gridlibrary/gdlib-db:${GDLIB_VERSION}
          docker push ${DOCKER_REGISTRY}/gridlibrary/gdlib-images:${GDLIB_VERSION}
          DOCKER_HOST=172.26.6.12 docker-compose up -d
        '''.stripIndent())       
    }
    publishers {
      buildPipelineTrigger('deployJob')
    }
}
