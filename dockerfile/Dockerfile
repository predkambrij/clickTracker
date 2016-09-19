FROM ubuntu:14.04

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && \
    apt-get upgrade -y && \
    echo "Europe/Ljubljana" > /etc/timezone && \
    dpkg-reconfigure -f noninteractive tzdata

# http://fabiorehm.com/blog/2014/09/11/running-gui-apps-with-docker/
RUN apt-get install -y x11-apps

# Replace with your user / group id
RUN export uid=499 gid=100 && \
    mkdir -p /home/developer && \
    echo "developer:x:${uid}:${gid}:Developer,,,:/home/developer:/bin/bash" >> /etc/passwd && \
    echo "developer:x:${uid}:" >> /etc/group && \
    echo "developer ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/developer && \
    chmod 0440 /etc/sudoers.d/developer && \
    chown ${uid}:${gid} -R /home/developer

# ssh https://docs.docker.com/examples/running_ssh_service/
#   and SSH login fix. Otherwise user is kicked off after login
RUN apt-get install -y openssh-server && \
    mkdir /var/run/sshd
                                                             
# dunno what's that
ENV NOTVISIBLE "in users profile"
RUN echo "export VISIBLE=now" >> /etc/profile

# basic utilities and fixes
RUN apt-get install -y screen && \
    chmod 775 /var/run/screen && \
                                 \
    apt-get install -y vim nano git && \
    apt-get install -y screen pwgen python-setuptools && \
    easy_install supervisor && \
    /bin/bash -c "echo 'developer:developerpw' | chpasswd"

ADD cfgs/dot_bashrc /home/developer/.bashrc
ADD cfgs/dot_bash_login /home/developer/.bash_login
RUN mkdir /i && \
    chown developer:users /i && \
    chown developer:users /home/developer/.bashrc && \
    chown developer:users /home/developer/.bash_login && \
    true

USER developer

RUN wget --no-cookies --header "Cookie: oraclelicense=foo" http://download.oracle.com/otn-pub/java/jdk/8u101-b13/jdk-8u101-linux-x64.tar.gz -O /i/jdk-8u101-linux-x64.tar.gz && \
    tar xzf /i/jdk-8u101-linux-x64.tar.gz -C /i && \
    rm /i/jdk-8u101-linux-x64.tar.gz && \
    # swt and stuff
    sudo apt-get install -y openjdk-7-jdk && \
    \
    echo 'export JAVA_HOME="/i/jdk1.8.0_101"' >> /home/developer/.bashrc && \
    \
    wget http://ftp-stud.fht-esslingen.de/pub/Mirrors/eclipse/technology/epp/downloads/release/neon/R/eclipse-jee-neon-R-linux-gtk-x86_64.tar.gz -P /i/ && \
    tar xzf /i/eclipse-jee-neon-R-linux-gtk-x86_64.tar.gz -C /i/ && \
    rm /i/eclipse-jee-neon-R-linux-gtk-x86_64.tar.gz && \
    \
    wget http://www.apache.si/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz -P /i/ && \
    tar xzf /i/apache-maven-3.3.9-bin.tar.gz -C /i/ && \
    rm /i/apache-maven-3.3.9-bin.tar.gz && \
    true

RUN wget https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-125.0.0-linux-x86_64.tar.gz -P /i/ && \
    tar xzf /i/google-cloud-sdk-125.0.0-linux-x86_64.tar.gz -C /i/ && \
    rm /i/google-cloud-sdk-125.0.0-linux-x86_64.tar.gz && \
    echo 'export PATH="/i/apache-maven-3.3.9/bin:$JAVA_HOME/bin:$PATH"' >> /home/developer/.bashrc && \
    echo -en "n\ny\n\n" | /i/google-cloud-sdk/install.sh && \
    bash -c ". ~/.bashrc; gcloud components install app-engine-java" && \
    mkdir -p ~/.config && \
    true

ADD cfgs/dot_boto /home/developer/.boto

USER root

EXPOSE 22 8080
CMD ["/usr/sbin/sshd", "-D"]
