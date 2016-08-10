job('buildRelease') {
    scm {
        git {
            remote {
                github('griddynamics/books-elibrary', 'ssh')
                credentials('github-cicd-key')
            }
            branch('master')
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
        shell('''
            cd grid-library-ui/dev/
            npm install -g gulp 
            npm install 
            npm rebuild node-sass
            npm install -g bower
            bower --allow-root install -g
            gulp config --apiUrl=prod1.gridlibrary.c4gd-orion.griddynamics.net \
                        --apiPort=8080 \
                        --imgUrl=prod1.gridlibrary.c4gd-orion.griddynamics.net \
                        --imgPort=8003
            gulp reload
            gulp prod

        '''.stripIndent())
        maven {
            goals('versions:set')
            properties(newVersion: '${APP_VERSION}.${BUILD_ID}')
        }
        maven { 
            goals('clean')
            goals('deploy -P releases')
            properties(skipTests: true)
            providedSettings('maven_settings')
    	  }
        downstreamParameterized {
            trigger('release') {
                parameters {
                    predefinedProps([
                        GL_BUILD_ID: '${BUILD_ID}',
                        GL_APP_VERSION: '${APP_VERSION}',
                    ])
                }
            }
        }
    }
}  
