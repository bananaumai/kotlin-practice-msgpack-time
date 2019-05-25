# Encode Kotlin/Java's Time(ZonedDateTime) to MessagePack
 
- There is timestamp extension format definition in [MessagePack specification](https://github.com/msgpack/msgpack/blob/master/spec.md#timestamp-extension-type)
- But [msgpack-java](https://github.com/msgpack/msgpack-java) does not support time extension format.
- This repository provide sample code to implement the puseudo code listed in MessagePack specification page.