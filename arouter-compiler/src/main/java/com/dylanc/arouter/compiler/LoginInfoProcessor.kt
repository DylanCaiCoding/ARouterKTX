package com.dylanc.arouter.compiler

import com.dylanc.arouter.annotations.LoginActivityPath
import com.squareup.javapoet.*
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

/**
 * @author Dylan Cai
 */
class LoginInfoProcessor : AbstractProcessor() {
  private lateinit var filer: Filer

  override fun init(processingEnv: ProcessingEnvironment) {
    super.init(processingEnv)
    filer = processingEnv.filer
  }

  override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
    val loginActivityPathElements = roundEnv.getElementsAnnotatedWith(LoginActivityPath::class.java).map { it as VariableElement }
    val typeSpec = TypeSpec.classBuilder(ClassName.get(PACKAGE_NAME, "$PROJECT${SEPARATOR}LoginInfo"))
      .addModifiers(Modifier.PUBLIC)
      .addField(FieldSpec.builder(String::class.java, "activityPath", Modifier.PUBLIC).build())
      .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
        .apply {
          if (loginActivityPathElements.isNotEmpty()) {
            if (loginActivityPathElements.size > 1) {
              throw IllegalStateException("There must be only one annotation of @LoginActivityPath.")
            }
            val element = loginActivityPathElements.first()
            addStatement("activityPath = ${element.fullClassName}.${element.simpleName}")
          }
        }
        .build())
      .build()

    try {
      JavaFile.builder(PACKAGE_NAME, typeSpec).build().writeTo(filer)
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return false
  }

  override fun getSupportedAnnotationTypes() = setOf(LoginActivityPath::class.java.canonicalName)

  override fun getSupportedSourceVersion() = SourceVersion.RELEASE_8

  private val VariableElement.fullClassName: String
    get() = ClassName.get(enclosingElement.asType()).toString()
}