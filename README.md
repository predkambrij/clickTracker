# Click Tracker

## Build and run Docker image for development
```git clone git@github.com:predkambrij/clickTracker.git; cd clickTracker```  
(replace "export uid=499 gid=100" in dockerfile/Dockerfile, so that it matches your user id and group id)  
```docker build -t predkambrij/u14devgae1 dockerfile/```  
```docker run -d -h devgae1.localdomain -p 127.1.0.3:1122:22 -p 127.1.0.3:8080:8080 -v $(pwd)/sparkjava:/proj -v $(pwd)/w:/w -v $(pwd)/dot_config_gcloud:/home/developer/.config/gcloud -v $(pwd)/dot_m2:/home/developer/.m2 -v /tmp/.X11-unix:/tmp/.X11-unix predkambrij/u14devgae1```  
```ssh developer@127.1.0.3 -p 1122```  

## Procedure for development (it allows frequent recompilation and restarting the server)
```cd /proj```  
(configure gcloud so that you can run integration tests and deploy to google app engine)  
```gcloud init```  

### Initial build of dependences
```mvn clean; mvn assembly:single```

### Recompile and start the server
```mvn compile && bash -c 'cd target/classes; java -Dlogback.configurationFile=../logback.xml -cp "../managed-vms-spark-1.0-jar-with-dependencies.jar:." org.blatnik.o7testproj.Main'```

## Build for appengine deployment
```mvn clean; mvn package; mvn gcloud:deploy -Dgcloud.gcloud_directory=/i/google-cloud-sdk```

## Tests
### Running tests (excluding integration tests which require access to datastore database)
```mvn -Dtest='!*IntegrationTest' -Dlogback.configurationFile=logback.xml test```

### Running only integration tests
```mvn -Dtest='*IntegrationTest' -Dlogback.configurationFile=logback.xml test```

### Running all tests
```mvn -Dlogback.configurationFile=logback.xml test```

### Eclipse text editor
(make sure that all dependences are downloaded)  
```mvn clean; mvn package```  
for workspace choose path "/w"  
File -> Open Projects from File System or Archive  
directory: /proj/src  
```screen -S ecl -dm bash -c "DISPLAY=:0.0 /i/eclipse/eclipse"```

## Campaigns
Note: make sure that local server is running or use app engine server name instead of "http://127.1.0.3:8080"

### Creation of new campaign
```curl -X POST 'http://127.1.0.3:8080/api/campaign' --user sadmin:sadminpw -iH 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -d 'name=testn&redirectUrl=http%3A%2F%2Fexample.org%2Fredirect1&platforms=iphone+android'```

### Update of existing campaign
```curl -X PUT 'http://127.1.0.3:8080/api/campaign/082c7290-396c-4610-a330-48ba8f18ffa0' --user sadmin:sadminpw -iH 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -d 'name=testm&redirectUrl=http%3A%2F%2Fexample.org%2Fredirect2&platforms=iphone+android'```

### Displaying information of existing campaign
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/api/campaign/082c7290-396c-4610-a330-48ba8f18ffa0```

### Listing existing campaigns
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/api/campaign```

### Listing existing campaigns available on given platform
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/api/campaign?platform=android```  
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/api/campaign?platform=iphone```

### Retreive number of clicks for given campaign
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/api/click/082c7290-396c-4610-a330-48ba8f18ffa0```

### Deletion of existing campaign
```curl --user sadmin:sadminpw -iX DELETE http://127.1.0.3:8080/api/campaign/082c7290-396c-4610-a330-48ba8f18ffa0```

## Tracking
### Handle incomming clicks (that contains information about campaign ID)
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/click/801b642a-577f-4c24-baf8-7e179adb6907```  

### Load test
```time for ((i=0;i<2000;i++)); do sleep 0.01; curl --user sadmin:sadminpw -X GET http://127.1.0.3:8080/click/801b642a-577f-4c24-baf8-7e179adb6907 & done```  
