/*
 * Copyright 2017-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization.protobuf

import kotlinx.serialization.*
import kotlinx.serialization.internal.ByteArraySerializer
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.test.*

class ByteArraySerializerTest {

    @Serializable
    class ByteArrayCarrier(@SerialId(2) @Serializable(with = ByteArraySerializer::class) val data: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as ByteArrayCarrier

            if (!data.contentEquals(other.data)) return false

            return true
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }

        override fun toString(): String {
            return "ByteArrayCarrier(data=${data.contentToString()})"
        }
    }

    @Test
    fun testByteArrayProtobuf() {
        val obj = ByteArrayCarrier(byteArrayOf(42, 100))
        val s = ProtoBuf.dumps(ByteArrayCarrier.serializer(), obj)
        assertEquals("""12022a64""", s)
        val obj2 = ProtoBuf.loads(ByteArrayCarrier.serializer(), s)
        assertEquals(obj, obj2)
    }

    @Test
    fun testWrappedByteArrayProtobuf() {
        val arraySize = 301
        val arr = Random.nextBytes(ByteArray(arraySize))
        val obj = ByteArrayCarrier(arr)
        val bytes = ProtoBuf.dump(ByteArrayCarrier.serializer(), obj)
        assertEquals(obj, ProtoBuf.load(ByteArrayCarrier.serializer(), bytes))
    }
}