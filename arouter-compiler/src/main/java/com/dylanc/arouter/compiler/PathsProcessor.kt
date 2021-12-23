package com.dylanc.arouter.compiler

import com.dylanc.arouter.annotations.RequireLoginPath
import com.squareup.javapoet.*
import java.io.IOException
import java.lang.RuntimeException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements

/**
 * @author Dylan Cai
 */
class PathsProcessor : AbstractProcessor() {
  private lateinit var filer: Filer
  private lateinit var elementUtils: Elements
  private var moduleName: String? = null

  override fun init(processingEnv: ProcessingEnvironment) {
    super.init(processingEnv)
    filer = processingEnv.filer
    elementUtils = processingEnv.elementUtils

    val options = processingEnv.options
    if (options.isNotEmpty()) {
      moduleName = options[KEY_MODULE_NAME]
    }
    if (!moduleName.isNullOrEmpty()) {
      moduleName = moduleName!!.replace("[^0-9a-zA-Z_]+".toRegex(), "")
      print("The user has configuration the module name, it was [$moduleName]")
    } else {
      throw RuntimeException("ARouter::Compiler >>> No module name, for more information, look at gradle log.")
    }
  }

  override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
    val checkLoginPathElements = roundEnv.getElementsAnnotatedWith(RequireLoginPath::class.java).map { it as VariableElement }
    val arrayListType = ParameterizedTypeName.get(ClassName.get(ArrayList::class.java), ClassName.get(String::class.java))
    val paramSpec = ParameterSpec.builder(arrayListType, "paths").build()
    val typeIRoutePaths = elementUtils.getTypeElement(IROUTE_PATHS)

    val loadIntoMethodBuilder = MethodSpec.methodBuilder("loadInto")
      .addAnnotation(Override::class.java)
      .addModifiers(Modifier.PUBLIC)
      .addParameter(paramSpec)

    checkLoginPathElements.forEach { element ->
      loadIntoMethodBuilder.addStatement("paths.add(${element.fullClassName}.${element.simpleName})")
    }

    val typeSpec = TypeSpec.classBuilder(ClassName.get(PACKAGE_NAME, "$PROJECT${SEPARATOR}Paths$SEPARATOR$moduleName"))
      .addSuperinterface(ClassName.get(typeIRoutePaths))
      .addModifiers(Modifier.PUBLIC)
      .addMethod(loadIntoMethodBuilder.build())
//      .addField(FieldSpec.builder(arrayListType, "requireLoginPaths", Modifier.PUBLIC).build())
//      .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
//        .apply {
//          addStatement("requireLoginPaths = new ArrayList<>()")
//          checkLoginPathElements.forEach { element ->
//            addStatement("requireLoginPaths.add(${element.fullClassName}.${element.simpleName})")
//          }
//        }
//        .build())
      .build()

    try {
      JavaFile.builder(PACKAGE_NAME, typeSpec).build().writeTo(filer)
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return false
  }

  override fun getSupportedAnnotationTypes() = setOf(RequireLoginPath::class.java.canonicalName)

  override fun getSupportedOptions() = setOf(KEY_MODULE_NAME)

  override fun getSupportedSourceVersion() = SourceVersion.RELEASE_8

  private val VariableElement.fullClassName: String
    get() = ClassName.get(enclosingElement.asType()).toString()
}