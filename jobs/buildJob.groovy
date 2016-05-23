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
        maven {
            goals('org.codehaus.mojo:versions-maven-plugin:2.1:set')
            properties(newVersion: "${BUILD_ID}")
        }
        maven { 
            goals('clean')
            goals('deploy -Pbuild,image')    
            providedSettings('maven_settings')
    	}
    }
}
