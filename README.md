
## Tracking
### Handle incomming clicks (that contains information about campaign ID)
## ```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/click/801b642a-577f-4c24-baf8-7e179adb6907```



## Campaigns
### Creation of new campaign
```curl -X POST 'http://127.1.0.3:8080/api/campaign' --user sadmin:sadminpw -iH 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -d 'name=testn&redirectUrl=http%3A%2F%2Fabc.net%2Fyesmmnnxxxyyddy&platforms=android'```

### Update of existing campaign
```curl -X PUT 'http://127.1.0.3:8080/api/campaign/082c7290-396c-4610-a330-48ba8f18ffa0' --user sadmin:sadminpw -iH 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -d 'name=testname&redirectUrl=http%3A%2F%2Fabc.net%2Fyesmmnna&platforms=android'```

### Deletion of existing campaign
```curl --user sadmin:sadminpw -iX DELETE http://127.1.0.3:8080/api/campaign/082c7290-396c-4610-a330-48ba8f18ffa0```

### Displaying information of existing campaign
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/api/campaign/082c7290-396c-4610-a330-48ba8f18ffa0```

### Listing existing campaigns
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/api/campaign```

### Listing existing campaigns available on given platform
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/api/campaign?platform=android```
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/api/campaign?platform=iphone```

### Retreiving number of clicks for given campaign
```curl --user sadmin:sadminpw -iX GET http://127.1.0.3:8080/api/click/082c7290-396c-4610-a330-48ba8f18ffa0```

### Record the click for the given campaign
```curl -iX GET http://127.1.0.3:8080/click/082c7290-396c-4610-a330-48ba8f18ffa0```


### Running tests (excluding integration tests which require access to datastore database)
```mvn -Dtest='!*IntegrationTest' -Dlogback.configurationFile=logback.xml test```
### Running only integration tests
```mvn -Dtest='*IntegrationTest' -Dlogback.configurationFile=logback.xml test```
### Running all tests
```mvn -Dlogback.configurationFile=logback.xml test```

