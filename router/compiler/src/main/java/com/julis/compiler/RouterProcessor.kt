package com.julis.compiler

import com.julis.annotation.Page
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class RouterProcessor : AbstractProcessor() {
    private var fileExist = false
    private var filer: Filer? = null

    private var supportedAnnotationTypes = setOf(
        Page::class.java.canonicalName,
    )

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        filer = processingEnv?.filer
        processingEnv?.messager?.printMessage(Diagnostic.Kind.NOTE, "--julis test")
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        println("RouterProcessor process start...")

        val mapMethod = MethodSpec.methodBuilder("register")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .addCode("\n")

        val activities: MutableSet<out Element>? = roundEnv?.getElementsAnnotatedWith(Page::class.java)
        activities?.forEach { element ->
            val host = getHost(element)
            val activityName = getActivityName(element)
            mapMethod.addStatement(buildPageStatement(host, activityName))
            println("pageAnnotation host:$host activity:$activityName")
        }
        println("RouterProcessor process end")

        val routerMapping: TypeSpec = TypeSpec.classBuilder(ROUTER_MAPPING_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(mapMethod.build())
            .build()

        if (!fileExist) {
            try {
                JavaFile.builder(ROUTER_PACKAGE_NAME, routerMapping)
                    .build()
                    .writeTo(filer)
                fileExist = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return true
    }

    private fun getHost(element: Element): String = element.getAnnotation(Page::class.java).value
    private fun getActivityName(element: Element): ClassName = ClassName.get(element as TypeElement)

    private fun buildPageStatement(host: String, activityName: ClassName): String =
        """$ROUTER_PACKAGE_NAME.Router.registerPage("$host", ${activityName}.class)"""

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): Set<String> {
        return supportedAnnotationTypes
    }

}