job('PreCommit') {
    scm {
        git {
            remote {
                github('griddynamics/books-elibrary', 'ssh')
                credentials('github-cicd-key')
                refspec('+refs/pull/*:refs/remotes/origin/pr/*')
            }
            branch('${sha1}')
        }
    }
    triggers {
        pullRequest {
            admin('anonymous')
            admin('aarutyunyan')
            cron('* * * * *')
            permitAll()
        }
    }
    steps {
      	maven('clean install -DskipTests')
    }
}
