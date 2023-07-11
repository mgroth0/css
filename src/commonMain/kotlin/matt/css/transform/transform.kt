package matt.css.transform

import matt.css.StyleDSLMarker
import matt.css.units.Length
import matt.css.units.toPercent
import matt.lang.inList
import matt.lang.require.requireNotEmpty
import matt.lang.require.requireNull
import matt.model.data.xyz.GenericXYZ
import matt.model.data.xyz.asTuple
import matt.model.data.xyz.genericXyz
import matt.model.data.xyz.toList
import matt.prim.str.lower
import kotlin.reflect.KClass

fun newTransform(op: Transform.() -> Unit) = Transform().apply(op)

@StyleDSLMarker
class Transform {
    companion object {

        private val transformFuns = mapOf<KClass<out TransformFun<*>>, (List<String>) -> TransformFun<*>>(
            Translate::class to { Translate(it.map { it.toString().toPercent() }.let { genericXyz(it[0], it[1]) }) },
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
                    val args = it.substringBefore(")").split(",")
                    t.funs.add(
                        transformFuns.entries.first { it.key.simpleName!!.lower() == funName!! }.value(args)
                    )
                    //		  t.map[funName!!] = args
                    funName = it.substringAfter(")").takeIf { it.isNotBlank() }?.trim()
                }
            }
            return t
        }
    }

    /*ORDER MATTERS, DUPLICATES POSSIBLE*/
    /*order matters and functions can be used multiple times!*/
    /*ORDER MATTERS, DUPLICATES POSSIBLE*/
    val funs = mutableListOf<TransformFun<*>>()
    /*ORDER MATTERS, DUPLICATES POSSIBLE*/

    sealed interface TransformFun<A> {
        val args: A
        fun parseArgs(args: List<String>): A
    }

    class Translate(override val args: GenericXYZ<Length?>) : TransformFun<GenericXYZ<Length?>> {

        init {
            requireNotEmpty(args.toList().filterNotNull())
            requireNotNull(args.x)
            if (args.y == null) requireNull(args.z)
        }

        override fun parseArgs(args: List<String>): GenericXYZ<Length?> {
            return args.map { it.toPercent() }.let {
                GenericXYZ(
                    it.getOrNull(0),
                    it.getOrNull(1),
                    it.getOrNull(2)
                )
            }
        }

        override fun toString(): String {

            return this::class.simpleName!! + args.asTuple(trimNullEnd = true, trailingComma = false)
        }
    }

    class Scale(override val args: Double) : TransformFun<Double> {
        override fun parseArgs(args: List<String>): Double {
            return args[0].toString().toDouble()
        }

        override fun toString(): String {
            return this::class.simpleName!! + "(" + args.inList().joinToString(",") + ") "
        }
    }

    //  private val transforms

    fun translate(
        x: Length,
        y: Length? = null,
        z: Length? = null
    ) = translate(genericXyz(x, y, z))

    fun translate(args: GenericXYZ<Length?>) {
        funs.add(Translate(args))
    }

    fun scale(args: Double) {
        funs.add(Scale(args))
    }

    //  var translate: Pair<matt.model.percent.Percent, matt.model.percent.Percent>
    //	get() = map["translate"]!!.map { it.toString().toPercent() }.let { it[0] to it[1] }
    //	set(value) {
    //	  map["translate"] = value.toList()
    //	}
    //  var scale: Double
    //	get() = map["scale"]!![0].toString().toDouble()
    //	set(value) {
    //	  map["scale"] = value.inList()
    //	}

    override fun toString(): String {
        var s = ""
        funs.forEach {
            s += it.toString()
            //	  s += it::class.simpleName!!.lower() + "("
            //	  s += it.args.joinToString(",")
            //	  s += ") "
        }
        return s
    }
}