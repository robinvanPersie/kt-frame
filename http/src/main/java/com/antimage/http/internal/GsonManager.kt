package com.antimage.http.internal

import com.antimage.http.annotation.Exclude
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Type

internal class GsonManager private constructor() {

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            GsonManager()
        }
    }

    val gson: Gson = GsonBuilder()
        .setExclusionStrategies(AnnotationExclusionStrategy())
        .registerTypeAdapterFactory(SafeTypeAdapterFactory())
        .registerTypeAdapter(Boolean::class.java, object : JsonDeserializer<Boolean> {
            override fun deserialize(
                json: JsonElement,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): Boolean {
                return try {
                    json.asInt == 1
                } catch (e: NumberFormatException) {
                    json.asBoolean
                }
            }
        })
        .create()

    private class SafeTypeAdapterFactory : TypeAdapterFactory {

        override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
            val delegate: TypeAdapter<T> = gson.getDelegateAdapter(this, type)
            return TypeAdapterImpl(delegate, type)
        }
    }

    private class TypeAdapterImpl<T>(
        private val delegate: TypeAdapter<T>,
        private val type: TypeToken<T>)
        : TypeAdapter<T>() {

        override fun write(out: JsonWriter?, value: T) {
            try {
                delegate.write(out, value)
            } catch (e: Exception) {
                delegate.write(out, null)
            }
        }

        override fun read(input: JsonReader): T? {
            try {
                return delegate.read(input)
            } catch (e: Exception) {
                input.skipValue()
                return null
            } catch (e: IllegalStateException) {
                input.skipValue()
                return null
            } catch (e: JsonSyntaxException) {
                input.skipValue()
                if (type.type is Class<*>) {
                    try {
                        return (type.type as Class<*>).newInstance() as T
                    } catch (e: Exception) {
                    }
                }
                return null
            } catch (e: NumberFormatException) {
                input.skipValue()
                return null
            }
        }
    }

    private class AnnotationExclusionStrategy : ExclusionStrategy {

        override fun shouldSkipField(f: FieldAttributes) = f.getAnnotation(Exclude::class.java) != null

        override fun shouldSkipClass(clazz: Class<*>): Boolean = false

    }
}