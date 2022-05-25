# JRunner  
Boilerplate of load testing java 8 tool  
Jmeter is cool, but sometimes it's faster and easier to write a couple lines of code  

This util works by schedule and there is a list of implemented things:  
* `html unit` as web driver  
* parametrization with files via `commons csv`  
* gathering data via writing it to `influx`  
* `junit` for refactor  
* logging to files via `log4j2`  
* schedule in config file  

### How to use
Create class that implements `Script` and does what you need  
Then call it by name in a schedule in config file


### Arguments  
No arguments needed, but you can pass anything and it will be in log files  
```java  
-name     ExampleRun  
```

### Start example  
```bat  
java -jar JRunner-1.0.jar -name ExampleRun  
```  

### Config example  
Config should be placed in resources of jar file   
```python  
#Influx connection data
influx.endpoint=http://localhost:8086
influx.database=jrunner
influx.retention=autogen
influx.batch=10
influx.user=admin
influx.pass=admin

#Scripts

#step is an action with args:
#start,x,y - start x users over y minutes
#wait,v - wait (keep script running) v minutes
#stop,z,q - stop z users over q minutes
#random pacing in ms (min,max)

scripts.count=2

script1.name=Correlation_Challenge_Mod
script1.pacing.enabled=true
script1.pacing.value=4800,5200
script1.steps=5
script1.step1=start,10,1
script1.step2=wait,5
script1.step3=start,10,1
script1.step4=wait,5
script1.step5=stop,20,1

script2.name=ExampleWebScript0
script2.pacing.enabled=true
script2.pacing.value=4800,5200
script2.steps=5
script2.step1=start,20,1
script2.step2=wait,5
script2.step3=start,20,1
script2.step4=wait,5
script2.step5=stop,40,1
```
