def script='''
export GDLIB_VERSION=${APP_VESRION}-${TIME_STAMP}-${BUILD_ID}
export NEXUS_REPOSITORY=pre-releases
export DOCKER_REGISTRY=172.26.6.4:5000
bash grid-library-containers/provide_artifacts.sh
docker-compose up -d
'''


job('dockerBuildJob') {
    steps {
        shell(script)
    }
}
