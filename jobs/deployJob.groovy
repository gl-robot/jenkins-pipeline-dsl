job('deployJob') {
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
            export GDLIB_VERSION=${GL_APP_VERSION}-${GL_TIME_STAMP}-${GL_BUILD_ID}
            export GDLIB_ENV=prod-docker
            export NEXUS_REPOSITORY=builds-all
            export NEXUS_STORAGE=http://172.26.30.4:8081/service/local/artifact/maven/redirect
            export GDLIB_REGISTRY_REPO=''
            cd grid-library-containers
            bash provide_artifacts.sh
            docker-compose build 
            export GDLIB_REGISTRY_REPO=172.26.30.4:5000/gridlibrary/
            docker login -u gridlibrary -p gridlibrary 172.26.30.4:5000
            for image in app db images; do
              docker tag gdlib-${image}:${GDLIB_VERSION} ${GDLIB_REGISTRY_REPO}gdlib-${image}:${GL_APP_VERSION}.${BUILD_ID}
              docker push ${GDLIB_REGISTRY_REPO}gdlib-${image}:${GL_APP_VERSION}.${BUILD_ID}
            done;
            DOCKER_HOST=prod1.gridlibrary.c4gd-orion.griddynamics.net:2376 GDLIB_REGISTRY_REPO=172.26.30.4:5000/gridlibrary/ GDLIB_VERSION=${GL_APP_VERSION}.${BUILD_ID} docker-compose -f docker-compose-prod.yml up -d
            sleep 30
            DOCKER_HOST=prod1.gridlibrary.c4gd-orion.griddynamics.net:2376 GDLIB_VERSION=${GL_APP_VERSION}.${BUILD_ID} docker-compose -f docker-compose-prod.yml logs
        '''.stripIndent())       
    }
}
