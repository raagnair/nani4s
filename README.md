# nani4s

Nani means "what", and this library is my attempt to simplify my own life when it comes to writing the data layer of an application. 

# Philosphy
1. Common entity definition pattern
  a. With primary key definition
2. Easy generic "DbDriver" trait that has an intuitive api
3. Different implementations of DbDriver connecting to different db solutions

As of now, all I've got is connectivity to Astra's REST API. Astra's proposed Scala library was so intrusive to the entity model that I felt a general library was necessary for interoperability. 
