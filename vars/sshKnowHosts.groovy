// ========================================================================== //
//                         S S H   K n o w n   H o s t s
// ========================================================================== //

// Adds SSH Known hosts lines if not already present
//
// Can read from environment{} variable SSH_KNOWN_HOSTS or passed as an arg

def call(known_hosts='', name='') {
  withEnv(["SSH_KNOWN_HOSTS=$known_hosts"]){
    // only works on stages, not steps
    //when {
    //  not { environment name: 'SSH_KNOWN_HOSTS', value: '' }
    //}
    String label = "Adding SSH Known Hosts: $name"
    sh (
      label: "$label",
      script: '''#!/usr/bin/env bash
        set -euxo pipefail
        # convenient and dynamic but not secure:
        #
        #   ssh-keyscan github.com >> ~/.ssh/known_hosts
        #   ssh-keyscan gitlab.com >> ~/.ssh/known_hosts
        #   ssh-keyscan ssh.dev.azure.com >> ~/.ssh/known_hosts
        #   ssh-keyscan bitbucket.org >> ~/.ssh/known_hosts
        #
        # instead load them from revision controlled adjacent functions:
        #
        #   sshKnownHostsGitHub()
        #   sshKnownHostsGitLab()
        #   sshKnownHostsBitbucket()
        #   sshKnownHostsAzureDevOps()
        #
        # or if you want them more dynamic you can load them via an arg to this function or environment variable 'SSH_KNOWN_HOSTS' from a Jenkins secret to share across all pipelines
        # don't do this either
        #cat >> ~/.ssh/config <<EOF
#Host *
#  LogLevel DEBUG3
#  #CheckHostIP no  # used ssh-keyscan instead
#EOF
        SSH_KNOWN_HOSTS_FILE="${SSH_KNOWN_HOSTS_FILE:-${HOME:-$(cd && pwd)}/.ssh/known_hosts}"
        # if defined in Jenkinsfile environment() section
        if [ -n "${SSH_KNOWN_HOSTS:-}" ]; then
          mkdir -pv "${SSH_KNOWN_HOSTS_FILE%/*}"
          touch "$SSH_KNOWN_HOSTS_FILE"
          while read -r line; do
            if ! grep -Fxq "$line" "$SSH_KNOWN_HOSTS_FILE"; then
              echo "$line" >> "$SSH_KNOWN_HOSTS_FILE"
            fi
          done <<< "$SSH_KNOWN_HOSTS"
        fi
      '''
      //sshKnownHostsGitHub()
      //sshKnownHostsGitLab()
      //sshKnownHostsBitbucket()
      //sshKnownHostsAzureDevOps()
    )
  }
}
