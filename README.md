# CS171_MapReduce

Available Ports: 5000-5010

||CLI|PRM|Map1|Map2|Reducer|
|---|:---:|:---:|:---:|:---:|:---:|
|PORT #|5000|5004&5005|5001|5002|5003|

To Compile:
javac CLI.java
javac PRM.java


To Run:
java CLI
java PRM <node#> <config file>


Example:

Machine 1:
java CLI
java PRM 1 csil1

Machine 2:
java CLI
java PRM 2 csil2


Config Files provided:
csil1
csil2



Progress Report:

*finished prm- there still might be a bug with the logobject that is added (sometimes will add a previous logobject

*still need to test for consensus on multiple paxos requests at the same time

*finished stop, resume

*finished total, print, merge