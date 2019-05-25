package dev.bananaumai.practice.msgpack.time

import org.msgpack.core.MessageBufferPacker
import org.msgpack.core.MessagePack
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)

    val time = ZonedDateTime.parse("2019-05-24T09:09:09.999999999Z", formatter)

    println(time)

    val encoded = encodeTime(time)

    println(encoded.joinToString(" ") { "%02X".format(it) } )

    val packer = MessagePack.newDefaultBufferPacker()
    packer.packTime(time)

    println(packer.toByteArray().joinToString(" ") { "%02X".format(it) } )
}

fun MessageBufferPacker.packTime(time: ZonedDateTime) {
    val payload = encodeTime(time)
    packExtensionTypeHeader(-1, payload.size)
    writePayload(payload)
}

fun encodeTime(time: ZonedDateTime): ByteArray {
    val sec = time.toEpochSecond().toULong()
    val nano = time.nano

    if (sec.shr(34) == 0UL) {
        val data = nano.toULong().shl(34).or(sec)

        return if (data.and(0xffffffff00000000u) == 0UL) {
            data.toUInt().toByteArray()
        } else {
            data.toByteArray()
        }
    }

    return nano.toUInt().toByteArray() + sec.toULong().toByteArray()
}

fun UInt.toByteArray(isBigEndian: Boolean = true): ByteArray {
    var bytes = byteArrayOf()

    var n = this

    if (n == 0x00u) {
        bytes += n.toByte()
    } else {
        while (n != 0x00u) {
            val b = n.toByte()

            bytes += b

            n = n.shr(Byte.SIZE_BITS)
        }
    }

    val padding = 0x00u.toByte()
    var paddings = byteArrayOf()
    repeat(UInt.SIZE_BYTES - bytes.count()) {
        paddings += padding
    }

    return if (isBigEndian) {
        paddings + bytes.reversedArray()
    } else {
        paddings + bytes
    }
}

fun ULong.toByteArray(isBigEndian: Boolean = true): ByteArray {
    var bytes = byteArrayOf()

    var n = this

    if (n == 0x00UL) {
        bytes += n.toByte()
    } else {
        while (n != 0x00UL) {
            val b = n.toByte()

            bytes += b

            n = n.shr(Byte.SIZE_BITS)
        }
    }

    val padding = 0x00.toByte()
    var paddings = byteArrayOf()
    repeat(ULong.SIZE_BYTES - bytes.count()) {
        paddings += padding
    }

    return if (isBigEndian) {
        paddings + bytes.reversedArray()
    } else {
        paddings + bytes
    }
}
