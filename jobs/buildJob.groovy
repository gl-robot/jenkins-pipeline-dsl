job('build') {
    scm {
        git {
            remote {
                github('griddynamics/books-elibrary', 'ssh')
                credentials('github-cicd-key')
            }
            branch('dev')
        }
    }   
    triggers {
        scm('* * * * *') 
    }   
    wrappers {
        environmentVariables {
            groovy(readFileFromWorkspace('resources/inject_env_vars.groovy'))
        }
    }

    steps {    
        maven {
            goals('org.codehaus.mojo:versions-maven-plugin:2.1:set')
            properties(newVersion: '${APP_VERSION}-${TIME_STAMP}-${BUILD_ID}')
        }

        maven { 
            goals('clean')
            goals('deploy -P tests')
            properties(skipTests: true)
            providedSettings('maven_settings')
    	}


        downstreamParameterized {
            trigger('dockerBuildJob') {
                parameters {
                    predefinedProps([
                        GL_BUILD_ID: '${BUILD_ID}',
                        GL_APP_VERSION: '${APP_VERSION}',
                        GL_TIME_STAMP: '${TIME_STAMP}'
                    ])
                }
            }
        }
    }
}  
