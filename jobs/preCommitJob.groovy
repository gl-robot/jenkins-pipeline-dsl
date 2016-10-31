job('preCommit') {
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
        githubPullRequest {
            admin('anonymous')
            admin('aarutyunyan')
            cron('* * * * *')
            permitAll()
            extensions {
                commitStatus {
                    statusUrl('http://jenkins.gridlibrary.c4gd-orion.griddynamics.net:8080/')
                }
            }
        }
    }
    steps {
      	maven('clean install')
    }
}
