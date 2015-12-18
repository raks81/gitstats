from ubuntu 

#Install Java
RUN sudo apt-get install -y software-properties-common \
	&& sudo add-apt-repository ppa:openjdk-r/ppa \
	&& sudo apt-get update \
	&& sudo apt-get install -y openjdk-8-jdk \
	&& java -version

# Install Glassfish
ENV JAVA_HOME         /usr/lib/jvm/java-8-openjdk-amd64
ENV GLASSFISH_HOME    /usr/local/glassfish4
ENV PATH              $PATH:$JAVA_HOME/bin:$GLASSFISH_HOME/bin
RUN apt-get install -y curl unzip zip inotify-tools && \
            rm -rf /var/lib/apt/lists/*
RUN         curl -L -o /tmp/glassfish-4.1.zip http://download.java.net/glassfish/4.1/release/glassfish-4.1.zip && \
            unzip /tmp/glassfish-4.1.zip -d /usr/local && \
            rm -f /tmp/glassfish-4.1.zip
EXPOSE      8080 4848 8181



# Install mysql
# add our user and group first to make sure their IDs get assigned consistently, regardless of whatever dependencies get added
RUN groupadd -r mysql && useradd -r -g mysql mysql

RUN  apt-get update \
	&& apt-get install -y perl pwgen --no-install-recommends \
	&& rm -rf /var/lib/apt/lists/*
RUN apt-key adv --keyserver ha.pool.sks-keyservers.net --recv-keys A4A9406876FCBD3C456770C88C718D3B5072E1F5
ENV MYSQL_MAJOR 5.5
ENV MYSQL_VERSION 5.5.47

RUN curl -SL "http://dev.mysql.com/get/Downloads/MySQL-$MYSQL_MAJOR/mysql-$MYSQL_VERSION-linux2.6-x86_64.tar.gz" -o mysql.tar.gz \
	&& curl -SL "http://mysql.he.net/Downloads/MySQL-$MYSQL_MAJOR/mysql-$MYSQL_VERSION-linux2.6-x86_64.tar.gz.asc" -o mysql.tar.gz.asc \
	&& mkdir /usr/local/mysql \
	&& tar -xzf mysql.tar.gz -C /usr/local/mysql --strip-components=1 \
	&& rm mysql.tar.gz* \
	&& rm -rf /usr/local/mysql/mysql-test /usr/local/mysql/sql-bench \
	&& rm -rf /usr/local/mysql/bin/*-debug /usr/local/mysql/bin/*_embedded \
	&& find /usr/local/mysql -type f -name "*.a" -delete \
	&& apt-get update && apt-get install -y binutils && rm -rf /var/lib/apt/lists/* \
	&& { find /usr/local/mysql -type f -executable -exec strip --strip-all '{}' + || true; }
ENV PATH $PATH:/usr/local/mysql/bin:/usr/local/mysql/scripts
RUN mkdir -p /etc/mysql/conf.d \
	&& { \
		echo '[mysqld]'; \
		echo 'skip-host-cache'; \
		echo 'skip-name-resolve'; \
		echo 'user = mysql'; \
		echo 'datadir = /var/lib/mysql'; \
		echo '!includedir /etc/mysql/conf.d/'; \
	} > /etc/mysql/my.cnf

RUN mkdir /var/lib/mysql
RUN chown mysql:mysql /var/lib/mysql
RUN apt-get update && apt-get install libaio1 libaio-dev
RUN mysql_install_db -user=mysql   -ldata=/var/lib/mysql/ --tmpdir=/tmp --basedir=/usr/local/mysql/


#COPY mysql/docker-entrypoint.sh /entrypoint.sh
EXPOSE 3306


CMD tail -f /dev/null
