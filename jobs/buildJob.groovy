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
      	maven('clean install -DskipTests')
    }
}
