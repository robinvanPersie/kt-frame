package com.antimage.http.internal

import android.util.Log
import com.antimage.http.Response
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonWriter
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.OutputStreamWriter
import java.io.Writer
import java.lang.Exception
import java.lang.reflect.Type
import java.nio.charset.Charset

internal class Convert {

    internal class ResponseConverterFactory private constructor(private val gson: Gson): Converter.Factory() {

        companion object {

            fun create() = this.create(Gson())

            fun create(gson: Gson) = ResponseConverterFactory(gson)
        }

        override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *>? {
//            return super.responseBodyConverter(type, annotations, retrofit)
            return ResponseBodyConverter(gson, type, gson.getAdapter(TypeToken.get(type)))
        }

        override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<*, RequestBody>? {
            return RequestBodyConverter(gson, gson.getAdapter(TypeToken.get(type)))
        }
    }

    private class RequestBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<T, RequestBody> {

        private val mediaType = "application/json; charset=UTF-8".toMediaTypeOrNull()

        override fun convert(value: T): RequestBody {
            val buffer = Buffer()
            val writer: Writer = OutputStreamWriter(buffer.outputStream(), Charset.forName("UTF-8"))
            val jsonWrite = gson.newJsonWriter(writer)
            adapter.write(jsonWrite, value)
            jsonWrite.close()
            return buffer.readByteString().toRequestBody(mediaType)
        }
    }

    private class ResponseBodyConverter<T> constructor(private val gson: Gson, private val type: Type, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {

        override fun convert(value: ResponseBody): T {
            val responseStr = value.string()
            var response: T = gson.fromJson(responseStr, type)
            return if (response is Response<*>) {
                if (response.isSuccess()) {
                    response
                } else {
                    throw Exception(response.msg)
                }
            } else {
                response
            }
        }
    }
}