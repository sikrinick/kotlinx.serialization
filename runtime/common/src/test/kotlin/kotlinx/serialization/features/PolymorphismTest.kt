/*
 * Copyright 2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kotlinx.serialization.features

import kotlinx.serialization.*
import kotlinx.serialization.context.*
import kotlinx.serialization.json.*
import kotlinx.serialization.protobuf.*
import kotlin.test.*

class PolymorphicTest {

    @Serializable
    data class Wrapper(@SerialId(1) val polyBase1: PolyBase, @SerialId(2) val polyBase2: PolyBase)

    private val moduleInstaller: SerialFormat.() -> Unit = {
        // second is to run with PolyDerived alone in `testExplicit`
        install(BaseAndDerivedModule + PolymorphicModule(PolyDerived::class, PolyDerived.serializer()))
    }

    private val json = Json(unquoted = true).apply(moduleInstaller)
    private val protobuf = ProtoBuf.apply(moduleInstaller)

    @Test
    fun testInheritanceJson() {
        val obj = Wrapper(PolyBase(2), PolyDerived("b"))
        val bytes = json.stringify(obj)
        assertEquals("{polyBase1:[kotlinx.serialization.features.PolyBase,{id:2}]," +
                "polyBase2:[kotlinx.serialization.features.PolyDerived,{id:1,s:b}]}", bytes)
    }

    @Test
    fun testInheritanceProtobuf() {
        val obj = Wrapper(PolyBase(2), PolyDerived("b"))
        val bytes = protobuf.dumps(obj)
        val restored = protobuf.loads<Wrapper>(bytes)
        assertEquals(obj, restored)
    }

    @Test
    fun testExplicit() {
        val obj = PolyDerived("b")
        val s = json.stringify(PolymorphicSerializer(PolyDerived::class), obj)
        assertEquals("[kotlinx.serialization.features.PolyDerived,{id:1,s:b}]", s)
    }
}