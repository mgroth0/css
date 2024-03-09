package matt.css.transform

import matt.css.StyleDSLMarker
import matt.css.ser.MarginSerializer
import matt.css.units.CssLength
import matt.lang.assertions.require.requireNotEmpty
import matt.lang.assertions.require.requireNull
import matt.lang.common.inList
import matt.lang.common.substringAfterSingular
import matt.lang.common.substringBeforeSingular
import matt.model.data.sensemod.DegreesDouble
import matt.model.data.xyz.GenericXYZ
import matt.model.data.xyz.asTuple
import matt.model.data.xyz.genericXyz
import matt.model.data.xyz.map
import matt.model.data.xyz.toList
import matt.prim.str.lower
import kotlin.reflect.KClass

fun newTransform(op: Transform.() -> Unit) = Transform().apply(op)

@StyleDSLMarker
class Transform {
    companion object {

        private val transformFuns =
            mapOf<KClass<out TransformFun<*>>, (List<String>) -> TransformFun<*>>(
                Translate::class to {
                    Translate(
                        it.map {
                            MarginSerializer.fromString(it) as CssLength
                        }.let {
                            check(it.size == 2)
                            genericXyz(it[0], it[1])
                        }
                    )
                },
                Scale::class to { Scale(it[0].toString().toDouble()) }
            )

        fun parse(s: String): Transform {
            val t = Transform()
            if (s.isBlank()) return t

            var funName: String? = null
            s.split("(").forEach {
                if (funName == null) {
                    funName = it.trim()
                } else {
                    val args = it.substringBeforeSingular(")").split(",")
                    t.funs.add(
                        transformFuns.entries.first { it.key.simpleName!!.lower() == funName!! }.value(args)
                    )
                    funName = it.substringAfterSingular(")").takeIf { it.isNotBlank() }?.trim()
                }
            }
            return t
        }
    }

    /*ORDER MATTERS, DUPLICATES POSSIBLE
    order matters and functions can be used multiple times!
    ORDER MATTERS, DUPLICATES POSSIBLE*/
    val funs = mutableListOf<TransformFun<*>>()
    /*ORDER MATTERS, DUPLICATES POSSIBLE*/

    sealed interface TransformFun<A> {
        val args: A
        fun parseArgs(args: List<String>): A
    }

    class Translate(override val args: GenericXYZ<CssLength?>) : TransformFun<GenericXYZ<CssLength?>> {

        init {
            requireNotEmpty(args.toList().filterNotNull())
            requireNotNull(args.x)
            if (args.y == null) requireNull(args.z)
        }

        override fun parseArgs(args: List<String>): GenericXYZ<CssLength?> =
            args.map {
                MarginSerializer.fromString(it) as CssLength
            }.let {
                check(it.size == 3)
                GenericXYZ(
                    it.getOrNull(0),
                    it.getOrNull(1),
                    it.getOrNull(2)
                )
            }

        override fun toString(): String =
            this::class.simpleName!!.lower() +
                args.map { it?.css }
                    .asTuple(trimNullEnd = true, trailingComma = false)
    }

    class Scale(override val args: Double) : TransformFun<Double> {
        override fun parseArgs(args: List<String>): Double = args[0].toDouble()

        override fun toString(): String = this::class.simpleName!!.lower() + "(" + args.inList().joinToString(",") + ") "
    }

    class Rotate(override val args: DegreesDouble) : TransformFun<DegreesDouble> {
        override fun parseArgs(args: List<String>): DegreesDouble {
            TODO()
        }

        override fun toString(): String = this::class.simpleName!!.lower() + "(" + args.asDouble + "deg" + ") "
    }

    fun rotate(degrees: DegreesDouble) {
        funs.add(Rotate(degrees))
    }


    fun translate(
        x: CssLength,
        y: CssLength? = null,
        z: CssLength? = null
    ) = translate(genericXyz(x, y, z))

    fun translate(args: GenericXYZ<CssLength?>) {
        funs.add(Translate(args))
    }

    fun scale(args: Double) {
        funs.add(Scale(args))
    }



    override fun toString(): String {
        var s = ""
        funs.forEach {
            s += it.toString()
        }
        return s
    }
}
