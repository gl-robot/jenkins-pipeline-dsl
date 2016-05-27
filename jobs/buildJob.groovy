job ('Build') {
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
    steps {
        shell('''
            APP_VERSION=`grep 'SNAPSHOT</version>' pom.xml | sed -r 's/.*<version>(.+)-.*/\1/'` 
            TIME_STAMP=$(date +%Y%m%d.%H%M%S)
            echo "APP_VERSION=${APP_VERSION}" >> version.properties
            echo "TIME_STAMP=${TIME_STAMP}" >> version.properties
        ''')

        configure { project ->
            project / builders / EnvInjectBuilder {
                propertiesFilePath("version.properties")
            }
        }

        maven {
            goals('org.codehaus.mojo:versions-maven-plugin:2.1:set')
            properties(newVersion: '${APP_VERSION}-${TIME_STAMP}-${BUILD_ID}')
        }

        maven { 
            goals('clean')
            goals('deploy -Ptests,image')    
            providedSettings('maven_settings')
    	}
    }
}
