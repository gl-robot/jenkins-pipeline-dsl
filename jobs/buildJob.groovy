job('Build') {
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
        shell(readFileFromWorkSpace('resources/tag_gen.sh')) 

        /*configure { project ->
            project / builders / EnvInjectBuilder {
                propertiesFilePath("version.properties")
            }
        }*/

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
